import java.io.*;
import java.util.*;

public class BuddyRequestVisitor extends Visitor
{
	public static final String VERB = "BUDDY_REQUEST";

	public BuddyRequestVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		User   requestee;
		String requesteeName;
		String requesterName;

		try
		{
			requesteeName = parts[1];
			requesterName = parts[2];
		    requestee = ctc.getServer().getUserTable().get(requesteeName);
		    if(requestee == null)
		    {
				ctc.getTalker().send(BuddyNotFoundVisitor.getCommand(requesteeName));
		    }
		    else
		    {
		        if(requestee.hasBuddy(requesterName))
		        {
					ctc.getTalker().send(BuddyAlreadyInListVisitor.getCommand(requesteeName));
			    }
			    else if(requestee.getCTC() == null)
			    {
					QueueHashtable requestQueueTable;

				    requestQueueTable = ctc.getServer().getBuddyRequestQueueTable();
				    if(requestQueueTable.get(requesteeName) != null)
				        requestQueueTable.get(requesteeName).add(requesterName);
				    else
				    {
						MyQueue<String> queue;
						queue = new MyQueue<String>();
						queue.add(requesterName);
						requestQueueTable.put(requesteeName, queue);
					}
				    ctc.getTalker().send(BuddyNotOnlineVisitor.getCommand(requesteeName));
			    }
			    else
			    {
					requestee.getCTC().getTalker().send(getCommand(requesterName));
				    ctc.getTalker().send(BuddyRequestReceivedVisitor.getCommand(requesteeName));
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
		int    result;
		String requester;
		String requestee;

		try
		{
			requester = parts[1];
			requestee = cts.getUsername();
			result    = MessageDialogUtilities.showBuddyRequestDialog(requester);

			if(result == MessageDialogUtilities.REQUEST_ACCEPTED)
			{
				cts.getTalker().send(BuddyRequestAcceptedVisitor.getCommand(requester, requestee));
			}
			else if(result == MessageDialogUtilities.REQUEST_DENIED)
			{
				cts.getTalker().send(BuddyRequestDeniedVisitor.getCommand(requester, requestee));
			}
			else if(result == MessageDialogUtilities.REQUEST_LATER)
			{
			    cts.getTalker().send(BuddyRequestProcrastinatedVisitor.getCommand(requestee, requester));
			}
		}
		catch(IOException ioe)
		{
			ioException.run();
		}
	}

	public static String getCommand(String requestee, String requester)
	{
		return TextConverter.merge(new String[]{VERB, requestee, requester});
	}

	public static String getCommand(String requester)
	{
		return TextConverter.merge(new String[]{VERB, requester});
	}
}