public class BuddyNotOnlineVisitor extends Visitor
{
	public static final String VERB = "BUDDY_NOT_ONLINE";

	public BuddyNotOnlineVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
	}

	@Override
	public void visit(CTS cts)
	{
		String requestee;
		requestee = parts[1];
		MessageDialogUtilities.showMessage("\"" + requestee + "\" is currently not online.\nYour request was received.", "Information");
	}

	public static String getCommand(String requestee)
	{
		return TextConverter.merge(new String[]{VERB, requestee});
	}
}