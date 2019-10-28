public class BuddyRequestReceivedVisitor extends Visitor
{
	public static final String VERB = "BUDDY_REQUEST_RECEIVED";

	public BuddyRequestReceivedVisitor(String[] parts, Runnable ioException)
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
		MessageDialogUtilities.showMessage("Your request was sent successfully.", "Information");
	}

	public static String getCommand(String requestee)
	{
		return TextConverter.merge(new String[]{VERB, requestee});
	}
}