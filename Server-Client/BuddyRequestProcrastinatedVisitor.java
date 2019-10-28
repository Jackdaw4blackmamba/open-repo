import java.util.*;
import java.io.*;

public class BuddyRequestProcrastinatedVisitor extends Visitor
{
	public static final String VERB = "BUDDY_REQUEST_PROCRASTINATED";

	public BuddyRequestProcrastinatedVisitor(String[] parts, Runnable ioException)
	{
        super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String procrastinator;
		String possibleBuddy;
		Hashtable<String, SetList<String>> possibleBuddyTable;

        try
        {
		    procrastinator = parts[1];
		    possibleBuddy  = parts[2];
		    possibleBuddyTable = ctc.getServer().getPossibleBuddyTable();
		    if(possibleBuddyTable.get(procrastinator) != null)
		        possibleBuddyTable.get(procrastinator).add(possibleBuddy);
		        //ctc.getServer().addPossibleBuddy(possibleBuddyTable.get(procrastinator), possibleBuddy);
		    else
		    {
			    SetList<String> list;
			    list = new SetList<String>();
			    list.add(possibleBuddy);
			    possibleBuddyTable.put(procrastinator, list);
		    }
		    ctc.getTalker().send(AddBuddyVisitor.getCommand(possibleBuddy));
		    ctc.getTalker().send(SetStatePendingVisitor.getCommand(possibleBuddy));

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
		String procrastinator;
		String possibleBuddy;

		try
		{
			procrastinator = parts[1];
			possibleBuddy  = parts[2];
			cts.getTalker().send(getCommand(procrastinator, possibleBuddy));
		}
		catch(IOException ioe)
		{
			ioException.run();
		}
	}

	public static String getCommand(String procrastinator, String possibleBuddy)
	{
		return TextConverter.merge(new String[]{VERB, procrastinator, possibleBuddy});
	}
}