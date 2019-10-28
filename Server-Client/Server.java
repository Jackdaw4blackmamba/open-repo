import java.net.*;
import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.*;
import java.security.cert.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.nio.channels.*;

public class Server
{
	private Hashtable<String, CTC>             ctcTable;
	private UserHashtable                      userTable;
	private Hashtable<String, SetList<Buddy>>  buddyTable;
	private Hashtable<String, SetList<String>> possibleBuddyTable;
	private QueueHashtable                     buddyRequestQueueTable;
	private QueueHashtable                     acceptedBuddyQueueTable;

    // This file contains all users saved.
	private static final String USERS_FILENAME            = "_users.dat";
	// This file contains possible buddies who were procrastinated by requestees.
	private static final String POSSIBLE_BUDDIES_FILENAME = "_possible_buddies.dat";
	// This file contains all buddy-requests which couldn't be delivered because receivers were offline.
	private static final String BUDDY_REQUESTS_FILENAME   = "_requests.dat";
	// This file contains all accepted users/buddies whose corresponding users/buddies were offline.
	private static final String ACCEPTED_BUDDIES_FILENAME = "_accepted_buddies.dat";

	public static void main(String[] args)
	{
		new Server();
	}

	public Server()
	{
		List<String> keys;

		ctcTable                = new Hashtable<String, CTC>();
		userTable               = loadUserHashtable(USERS_FILENAME);
		buddyTable              = new Hashtable<String, SetList<Buddy>>();
		possibleBuddyTable      = loadPossibleBuddyHashtable(POSSIBLE_BUDDIES_FILENAME);
		buddyRequestQueueTable  = loadQueueHashtable(BUDDY_REQUESTS_FILENAME);
		acceptedBuddyQueueTable = loadQueueHashtable(ACCEPTED_BUDDIES_FILENAME);

		keys = Arrays.asList(userTable.keySet().toArray(new String[]{}));
		for(String key : keys)
		    setBuddies(userTable.get(key), buddyTable);

        try
        {
            beginReceiving();
		}
		catch(Exception e)
		{
			MessageDialogUtilities.showErrorMessage("Error: failed to begin server...");
		}
	}

	private void beginReceiving() throws Exception
	{
		SSLServerSocket sslServerSocket;
		sslServerSocket = getSSLServerSocket(6789);
		while(true)
		{
			try
			{
			    SSLSocket sslSocket;
			    sslSocket = (SSLSocket)sslServerSocket.accept();
			    new CTC(new Talker(sslSocket), this);
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: IO exception...");
			}
			catch(SecurityException se)
			{
				MessageDialogUtilities.showErrorMessage("Error: security exception...");
			}
			catch(IllegalBlockingModeException ibme)
			{
				MessageDialogUtilities.showErrorMessage("Error: no connections available...");
			}
			catch(Exception e)
			{
				MessageDialogUtilities.showErrorMessage("Error: exception...");
			}
		}
	}

	private SSLServerSocket getSSLServerSocket(int port) throws Exception
	{
		SSLContext             sslContext;
		KeyManagerFactory      keyManagerFactory;
		KeyStore               keyStore;
		char[]                 keyStorePassphrase;

		SSLServerSocketFactory sslServerSocketFactory;
		SSLServerSocket        sslServerSocket;

		sslContext = SSLContext.getInstance("SSL");
		keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyStore = KeyStore.getInstance("JKS");
		keyStorePassphrase = "passphrase".toCharArray();
		keyStore.load(new FileInputStream("testkeys"), keyStorePassphrase);
		keyManagerFactory.init(keyStore, keyStorePassphrase);
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
		sslServerSocketFactory = sslContext.getServerSocketFactory();

		sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(port);

		Thread.sleep(1000);

		return sslServerSocket;
	}

	public void visitEachOnlineBuddyOf(String username, String visitorCommand) throws IOException
	{
		List<String> keys;

		keys = Arrays.asList(buddyTable.keySet().toArray(new String[]{}));
		for(String key : keys)
		{
			User user;
			user = userTable.get(key);
			if(user != null && user.getCTC() != null && user.hasBuddy(username))
			    user.getCTC().getTalker().send(visitorCommand);
		}
	}

	public void setOnlineState(String username, boolean online)
	{
		List<String> keys;

		keys = Arrays.asList(buddyTable.keySet().toArray(new String[]{}));
		for(String key : keys)
		{
			User user;
			user = userTable.get(key);
			if(user != null && user.hasBuddy(username))
			{
				SetList<Buddy> buddyList;
				buddyList = buddyTable.get(user.getName());
				if(buddyList != null)
				{
					for(int i = 0; i < buddyList.size(); i++)
					{
						if(buddyList.get(i).getName().equals(username))
							buddyList.get(i).setState(online ? Buddy.ONLINE : Buddy.OFFLINE);
					}
				}
			}
		}
	}

	public void deleteBuddy(User user, User buddy) throws IOException
	{
		if(userTable.get(user.getName()) != null)
		{
			SetList<Buddy> list;
			list = buddyTable.get(user.getName());
			for(int i = 0; i < list.size(); i++)
			    if(list.get(i).getName().equals(buddy.getName()))
			    {
					list.remove(i);
					if(user.getCTC() != null)
					    user.getCTC().getTalker().send(DeleteBuddyVisitor.getCommand(buddy.getName()));
					if(list.size() == 0)
					    buddyTable.remove(user.getName());
					break;
				}
		}
	}

	public void deletePossibleBuddy(User user, User buddy) throws IOException
	{
		if(possibleBuddyTable.get(user.getName()) != null)
		{
			SetList<String> list;
			list = possibleBuddyTable.get(user.getName());
			for(int i = 0; i < list.size(); i++)
				if(list.get(i).equals(buddy.getName()))
				{
					list.remove(i);
					if(user.getCTC() != null)
					    user.getCTC().getTalker().send(DeleteBuddyVisitor.getCommand(buddy.getName()));
					if(list.size() == 0)
					    possibleBuddyTable.remove(user.getName());
					break;
				}
		}
	}

	public Hashtable<String, CTC> getCTCTable()
	{
		return ctcTable;
	}

	public UserHashtable getUserTable()
	{
		return userTable;
	}

	public Hashtable<String, SetList<Buddy>> getBuddyTable()
	{
		return buddyTable;
	}

	public Hashtable<String, SetList<String>> getPossibleBuddyTable()
	{
		return possibleBuddyTable;
	}

	public QueueHashtable getBuddyRequestQueueTable()
	{
		return buddyRequestQueueTable;
	}

	public QueueHashtable getAcceptedBuddyQueueTable()
	{
		return acceptedBuddyQueueTable;
	}
/*
	public void addPossibleBuddy(List<String> possibleBuddyList, String possibleBuddyName)
	{
		boolean found;
		found = false;
		for(int i = 0; i < possibleBuddyList.size(); i++)
		    if(possibleBuddyList.get(i).equals(possibleBuddyName))
		    {
				found = true;
				break;
			}
		if(!found)
		    possibleBuddyList.add(possibleBuddyName);
	}
*/
	private UserHashtable loadUserHashtable(String filename)
	{
		UserHashtable table;
		if(new File(filename).exists())
		{
			try
			{
				table = new UserHashtable(new DataInputStream(new FileInputStream(filename)));
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: failed to load \"" + filename + "\"...");
				table = new UserHashtable();
			}
		}
		else
		    table = new UserHashtable();
		return table;
	}

	private Hashtable<String, SetList<String>> loadPossibleBuddyHashtable(String filename)
	{
		Hashtable<String, SetList<String>> table;
		DataInputStream dis;

		table = new Hashtable<String, SetList<String>>();
		if(new File(filename).exists())
		{
			try
			{
				dis = new DataInputStream(new FileInputStream(filename));
				while(dis.available() > 0)
				{
					String key;
					int    size;
					SetList<String> list;
					key = dis.readUTF();
					size = dis.readInt();
					list = new SetList<String>();
					for(int i = 0; i < size; i++)
					    list.add(dis.readUTF());
					table.put(key, list);
				}
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: failed to load \"" + filename + "\"...");
			}
		}
		return table;
	}

	private QueueHashtable loadQueueHashtable(String filename)
	{
		QueueHashtable queue;
		if(new File(filename).exists())
		{
			try
			{
				queue = new QueueHashtable(new DataInputStream(new FileInputStream(filename)));
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: failed to load \"" + filename + "\"...");
				queue = new QueueHashtable();
			}
		}
		else
		    queue = new QueueHashtable();
		return queue;
	}

	private void setBuddies(User user, Hashtable<String, SetList<Buddy>> buddyTable)
	{
		for(int i = 0; i < user.getBuddyNamesCount(); i++)
		{
			String buddyName;
			buddyName = user.getBuddyName(i);
			addBuddy(user.getName(), buddyName, buddyTable);
			addBuddy(buddyName, user.getName(), buddyTable);
		}
	}

	private void addBuddy(String username, String buddyName, Hashtable<String, SetList<Buddy>> buddyTable)
	{
		SetList<Buddy> list;
		list = buddyTable.get(username);
		if(list == null)
	    {
			SetList<Buddy> tmpList;
			tmpList = new SetList<Buddy>();
			tmpList.add(new Buddy(buddyName, Buddy.OFFLINE));
			buddyTable.put(username, tmpList);
		}
		else
		{
			boolean found;
			found = false;
			for(int j = 0; j < list.size(); j++)
			    if(list.get(j).getName().equals(buddyName))
				{
					found = true;
					break;
				}
			if(!found)
				list.add(new Buddy(buddyName, Buddy.OFFLINE));
		}
	}

	public void saveCurrentData()
	{
		DataOutputStream userTableDos;
		DataOutputStream possibleTableDos;
		DataOutputStream requestQueueDos;
		DataOutputStream acceptedQueueDos;
		List<String>     keys;

		try
		{
			userTableDos     = new DataOutputStream(new FileOutputStream(USERS_FILENAME));
			possibleTableDos = new DataOutputStream(new FileOutputStream(POSSIBLE_BUDDIES_FILENAME));
			requestQueueDos  = new DataOutputStream(new FileOutputStream(BUDDY_REQUESTS_FILENAME));
			acceptedQueueDos = new DataOutputStream(new FileOutputStream(ACCEPTED_BUDDIES_FILENAME));

			userTable.store(userTableDos);
            keys = Arrays.asList(possibleBuddyTable.keySet().toArray(new String[]{}));
            for(int i = 0; i < keys.size(); i++)
            {
				SetList<String> list;
				possibleTableDos.writeUTF(keys.get(i));
				list = possibleBuddyTable.get(keys.get(i));
				possibleTableDos.writeInt(list.size());
				for(int j = 0; j < list.size(); j++)
				    possibleTableDos.writeUTF(list.get(j));
			}
			buddyRequestQueueTable.store(requestQueueDos);
	        acceptedBuddyQueueTable.store(acceptedQueueDos);

	        userTableDos.close();
	        possibleTableDos.close();
	        requestQueueDos.close();
	        acceptedQueueDos.close();
		}
		catch(IOException ioe)
		{
			MessageDialogUtilities.showErrorMessage("Error: failed to save data...");
		}
	}
}