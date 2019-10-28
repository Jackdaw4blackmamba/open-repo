import java.io.*;

public class BuddyRequestDeniedVisitor extends Visitor
{
	public static final String VERB = "BUDDY_REQUEST_DENIED";

	public BuddyRequestDeniedVisitor(String[] parts, Runnable ioException)
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

        try
        {
		    requesterName = parts[1];
		    requesteeName = parts[2];
		    requester = ctc.getServer().getUserTable().get(requesterName);
		    requestee = ctc.getServer().getUserTable().get(requesteeName);

		    if(requester != null && requestee != null)
		    {
			    ctc.getServer().deletePossibleBuddy(requester, requestee);
			    ctc.getServer().deletePossibleBuddy(requestee, requester);

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
}