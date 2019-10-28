public class BuddyAlreadyInListVisitor extends Visitor
{
	public static final String VERB = "BUDDY_ALREADY_IN_LIST";

	public BuddyAlreadyInListVisitor(String[] parts, Runnable ioException)
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
		MessageDialogUtilities.showMessage("\"" + requestee + "\" is already a buddy of yours.", "Information");
	}

	public static String getCommand(String requestee)
	{
		return TextConverter.merge(new String[]{VERB, requestee});
	}
}