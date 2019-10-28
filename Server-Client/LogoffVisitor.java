import java.util.*;
import java.io.*;

public class LogoffVisitor extends Visitor
{
	public static final String VERB = "LOGOFF";

	public LogoffVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String username;
		User user;

        try
        {
		    username = parts[1];
		    ctc.getServer().getCTCTable().remove(username);
		    user = ctc.getServer().getUserTable().get(username);
		    if(user != null)
			    user.setCTC(null);

		    ctc.getServer().visitEachOnlineBuddyOf(username, SetStateOfflineVisitor.getCommand(username));
		    ctc.getServer().setOnlineState(username, false);
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
}