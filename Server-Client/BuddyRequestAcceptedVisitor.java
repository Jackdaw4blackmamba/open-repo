import java.io.*;
import java.util.*;

public class BuddyRequestAcceptedVisitor extends Visitor
{
	public static final String VERB = "BUDDY_REQUEST_ACCEPTED";

	public BuddyRequestAcceptedVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String requesterName;
		String requesteeName;
		User   requester;
		User   requestee;
	    Hashtable<String, SetList<Buddy>> buddyTable;

		try
		{
			requesterName = parts[1];
			requesteeName = parts[2];

		    requester = ctc.getServer().getUserTable().get(requesterName);
		    requestee = ctc.getServer().getUserTable().get(requesteeName);

            if(requester != null && requestee != null)
            {
				requester.addBuddyName(requestee.getName());
				requestee.addBuddyName(requester.getName());

				ctc.getServer().deletePossibleBuddy(requester, requestee);
                ctc.getServer().deletePossibleBuddy(requestee, requester);

                // Client updates
				reflectBuddyToClient(ctc.getServer(), requester, requestee);
				reflectBuddyToClient(ctc.getServer(), requestee, requester);

                // Server updates
				buddyTable = ctc.getServer().getBuddyTable();
				updateBuddyTable(buddyTable, requester, requestee);
				updateBuddyTable(buddyTable, requestee, requester);

			    ctc.getServer().saveCurrentData();
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
	}

	public static String getCommand(String requester, String requestee)
	{
		return TextConverter.merge(new String[]{VERB, requester, requestee});
	}

	private void reflectBuddyToClient(Server server, User user, User buddy) throws IOException
	{
		if(user.getCTC() != null)
		{
			user.getCTC().getTalker().send(AddBuddyVisitor.getCommand(buddy.getName()));
			if(buddy.getCTC() != null)
				user.getCTC().getTalker().send(SetStateOnlineVisitor.getCommand(buddy.getName()));
			else
			    user.getCTC().getTalker().send(SetStateOfflineVisitor.getCommand(buddy.getName()));
			user.getCTC().getTalker().send(SetColorVisitor.getCommand(buddy.getName(), Buddy.DEFAULT_COLOR));
		}
		else
		{
			QueueHashtable acceptedQueueTable;
			acceptedQueueTable = server.getAcceptedBuddyQueueTable();

			if(acceptedQueueTable.get(user.getName()) != null)
			    acceptedQueueTable.get(user.getName()).add(buddy.getName());
			else
			{
				MyQueue<String> queue;
				queue = new MyQueue<String>();
				queue.add(buddy.getName());
				acceptedQueueTable.put(user.getName(), queue);
			}
		}
	}

	private void updateBuddyTable(Hashtable<String, SetList<Buddy>> buddyTable, User user, User buddy) throws IOException
	{
		if(buddyTable.get(user.getName()) != null)
			buddyTable.get(user.getName()).add(new Buddy(buddy.getName(), (buddy.getCTC() != null ? Buddy.ONLINE : Buddy.OFFLINE)));
		else
		{
			SetList<Buddy> list;
			list = new SetList<Buddy>();
			list.add(new Buddy(buddy.getName(), (buddy.getCTC() != null ? Buddy.ONLINE : Buddy.OFFLINE)));
			buddyTable.put(user.getName(), list);
		}
	}
/*
	public static String getCommand(String requestee)
	{
		return TextConverter.merge(new String[]{VERB, requestee});
	}
*/
}