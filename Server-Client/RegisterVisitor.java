import java.util.*;
import java.io.*;

public class RegisterVisitor extends Visitor
{
	public static final String VERB = "REGISTER";

	public RegisterVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String        username;
		UserHashtable userTable;

        try
        {
		    username = parts[1];
		    userTable = ctc.getServer().getUserTable();
		    if(userTable.get(username) != null)
		    {
			    ctc.getTalker().send(RegisterDeniedVisitor.getCommand("Error: The user \"" + username + "\" already exists."));
			    //suggest possible names
			    //readyToQuit = true;
		    }
		    else
		    {
			    User user;
			    user = new User();
			    user.setName(username);
			    user.setEncryptedPassword(parts[2]);
			    user.setCTC(null);
			    userTable.put(username, user);
			    ctc.getTalker().send(TextConverter.merge(new String[]{VERB}));
			    ctc.getServer().getCTCTable().put(username, ctc);
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
	    MessageDialogUtilities.showMessage("Registered successfully.", "Confirmation");
	}

	public static String getCommand(String username, String password)
	{
		return TextConverter.merge(new String[]{VERB, username, password});
	}
}