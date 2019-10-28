import java.util.*;
import java.io.*;

public class ClientViewConstructedVisitor extends Visitor
{
	public static final String VERB = "CLIENT_VIEW_CONSTRUCTED";

	public ClientViewConstructedVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String       username;
		SetList<Buddy>  buddyList;
		SetList<String> possibleBuddyNameList;

		try
		{
			username = parts[1];

			checkQueues(ctc, username);

		    buddyList = ctc.getServer().getBuddyTable().get(username);
		    if(buddyList != null)
		    {
				for(int i = 0; i < buddyList.size(); i++)
				{
					Buddy buddy;
					buddy = buddyList.get(i);
					ctc.getTalker().send(AddBuddyVisitor.getCommand(buddy.getName()));
					if(buddy.getState() == Buddy.ONLINE)
						ctc.getTalker().send(SetStateOnlineVisitor.getCommand(buddy.getName()));
					else if(buddy.getState() == Buddy.OFFLINE)
						ctc.getTalker().send(SetStateOfflineVisitor.getCommand(buddy.getName()));
				    else
				        ctc.getTalker().send(SetStatePendingVisitor.getCommand(buddy.getName()));
					ctc.getTalker().send(SetColorVisitor.getCommand(buddy.getName(), buddy.getColor()));
				}
			}

			possibleBuddyNameList = ctc.getServer().getPossibleBuddyTable().get(username);
			if(possibleBuddyNameList != null)
			{
				for(int i = 0; i < possibleBuddyNameList.size(); i++)
				    ctc.getTalker().send(BuddyRequestProcrastinatedVisitor.getCommand(username, possibleBuddyNameList.get(i)));
			}

			ctc.getServer().saveCurrentData();
		}
		catch(IOException ioe)
		{
            ioException.run();
		}
	}

	@Override
	public void visit(CTS cts)
	{
	}

	public static String getCommand(String username)
	{
		return TextConverter.merge(new String[]{VERB, username});
	}

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
}