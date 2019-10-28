import java.io.*;
import java.util.*;

public class LoginVisitor extends Visitor
{
	public static final String VERB = "LOGIN";

	public LoginVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String username;
		String password;
		User   user;

		try
		{
		    username = parts[1];
		    password = parts[2];
		    user = ctc.getServer().getUserTable().get(username);
		    if(user == null)
		    {
			    ctc.getTalker().send(LoginDeniedVisitor.getCommand("Error: wrong username..."));
			    //readyToQuit = true;
			}
			else
			{
		        if(!user.getEncryptedPassword().equals(password))
		        {
					ctc.getTalker().send(LoginDeniedVisitor.getCommand("Error: wrong password..."));
					//readyToQuit = true;
				}
				else
				{
					if(user.getCTC() != null && user.getCTC() != ctc)
					    ctc.getTalker().send(LoginDeniedVisitor.getCommand("Error: " + username + " already logged in..."));
					else
					{
					    user.setCTC(ctc);
					    ctc.getTalker().send(getCommand(username));
					    ctc.getServer().visitEachOnlineBuddyOf(username, SetStateOnlineVisitor.getCommand(username));
					    ctc.getServer().setOnlineState(username, true);

					    //checkQueues(ctc, username);

					    //ctc.getServer().saveCurrentData();
					}
				}
		    }
		}
		catch(IOException ioe)
		{
			ioException.run();
		}
	}

	@Override
	public void visit(CTS cts)
	{
		String      username;
		EntryDialog dialog;

		username = parts[1];
		dialog = cts.getEntryDialog();
		MessageDialogUtilities.showMessage("Logged in successfully.", "Confirmation");
		dialog.setUsername(username);
		cts.setUsername(username);
		dialog.dispose();
		cts.setEntryDialog(null);
	}

	public static String getCommand(String username, String password)
	{
		return TextConverter.merge(new String[]{VERB, username, password});
	}

	public static String getCommand(String username)
	{
		return TextConverter.merge(new String[]{VERB, username});
	}
/*
	private void checkQueues(CTC ctc, String username) throws IOException
	{
		MyQueue<String> buddyRequestQueue;
		MyQueue<String> acceptedBuddyQueue;

		buddyRequestQueue = ctc.getServer().getBuddyRequestQueueTable().get(username);
		acceptedBuddyQueue = ctc.getServer().getAcceptedBuddyQueueTable().get(username);

        if(buddyRequestQueue != null)
        {
            while(buddyRequestQueue.hasNext())
            {
				String buddyName;
				buddyName = buddyRequestQueue.poll();
				ctc.getTalker().send(BuddyRequestVisitor.getCommand(username, buddyName));
			}
		}

		if(acceptedBuddyQueue != null)
		{
			User user;
			user = ctc.getServer().getUserTable().get(username);
			if(user != null)
			{
				while(acceptedBuddyQueue.hasNext())
				{
					String buddyName;
					User   buddy;
					buddyName = acceptedBuddyQueue.poll();
					buddy     = ctc.getServer().getUserTable().get(buddyName);
					user.addBuddyName(buddyName);
					ctc.getTalker().send(AddBuddyVisitor.getCommand(buddyName));
					if(buddy != null)
					{
						if(buddy.getCTC() != null)
                            ctc.getTalker().send(SetStateOnlineVisitor.getCommand(buddyName));
                        else
                            ctc.getTalker().send(SetStateOfflineVisitor.getCommand(buddyName));
					}
					ctc.getTalker().send(SetColorVisitor.getCommand(buddyName, Buddy.DEFAULT_COLOR));
				}
			}
		}
	}
	*/
}