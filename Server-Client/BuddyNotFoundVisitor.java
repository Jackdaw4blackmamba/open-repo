public class BuddyNotFoundVisitor extends Visitor
{
	public static final String VERB = "BUDDY_NOT_FOUND";

	public BuddyNotFoundVisitor(String[] parts, Runnable ioException)
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
		MessageDialogUtilities.showErrorMessage("\"" + requestee + "\" was not found.");
	}

	public static String getCommand(String requestee)
	{
		return TextConverter.merge(new String[]{VERB, requestee});
	}
}