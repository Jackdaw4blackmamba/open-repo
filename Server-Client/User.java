import java.util.*;
import java.io.*;

public class User
{
	private String       name;
	private String       encryptedPasswd;
	private Hashtable<String, Buddy> buddyTable;
	private List<String> buddyNameList;
	private CTC          ctc;

	public User()
	{
		init();
	}

	public User(DataInputStream dis) throws IOException
	{
        int listSize;

        init();

        listSize        = dis.readInt();
        name            = dis.readUTF();
        encryptedPasswd = dis.readUTF();
        for(int i = 0; i < listSize; i++)
            buddyNameList.add(dis.readUTF());
	}

	private void init()
	{
		name = "";
		encryptedPasswd = "";
		buddyTable = new Hashtable<String, Buddy>();
		buddyNameList = new ArrayList<String>();
		ctc = null;
	}

	public void store(DataOutputStream dos) throws IOException
	{
		dos.writeInt(buddyNameList.size());
		dos.writeUTF(name);
		dos.writeUTF(encryptedPasswd);
		for(int i = 0; i < buddyNameList.size(); i++)
		    dos.writeUTF(buddyNameList.get(i));
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setEncryptedPassword(String encryptedPasswd)
	{
		this.encryptedPasswd = encryptedPasswd;
	}

	public String getEncryptedPassword()
	{
		return encryptedPasswd;
	}

	public void addBuddyName(String buddyName)
	{
		buddyNameList.add(buddyName);
	}

	public String getBuddyName(int index)
	{
		return buddyNameList.get(index);
	}

	public void removeBuddyName(int index)
	{
		buddyNameList.remove(index);
	}

	public void clearBuddyNameList()
	{
		buddyNameList.clear();
	}

	public int getBuddyNamesCount()
	{
		return buddyNameList.size();
	}

/*
    public void addBuddy(String buddyName, Buddy buddy)
    {
		buddyTable.put(buddyName, buddy);
	}

	public Buddy getBuddy(String buddyName)
	{
		return buddyTable.get(buddyName);
	}
*/
	public boolean hasBuddy(String possibleBuddyName)
	{
		return buddyNameList.contains(possibleBuddyName);
	}

	public void setCTC(CTC ctc)
	{
		this.ctc = ctc;
	}

	public CTC getCTC()
	{
		return ctc;
	}

	@Override
	public boolean equals(Object user)
	{
		return getName().equals(((User)user).getName());
	}
}