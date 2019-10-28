import java.io.*;

public class UnfriendVisitor extends Visitor
{
	public static final String VERB = "UNFRIEND";

	public UnfriendVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String username;
		String buddyName;
		User   user;
		User   buddy;

		try
		{
		    username  = parts[1];
		    buddyName = parts[2];
		    user      = ctc.getServer().getUserTable().get(username);
		    buddy     = ctc.getServer().getUserTable().get(buddyName);

		    if(user != null && buddy != null)
		    {
				deleteBuddyFromUser(user, buddy.getName());
				deleteBuddyFromUser(buddy, user.getName());

				ctc.getServer().deleteBuddy(user, buddy);
				ctc.getServer().deleteBuddy(buddy, user);

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

	public static String getCommand(String username, String buddyNameToDelete)
	{
		return TextConverter.merge(new String[]{VERB, username, buddyNameToDelete});
	}

	private void deleteBuddyFromUser(User user, String buddyName)
	{
		for(int i = 0; i < user.getBuddyNamesCount(); i++)
		    if(user.getBuddyName(i).equals(buddyName))
		    {
				user.removeBuddyName(i);
				break;
			}
	}
}