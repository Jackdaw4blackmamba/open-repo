public class LoginDeniedVisitor extends Visitor
{
	public static final String VERB = "LOGIN_DENIED";

	public LoginDeniedVisitor(String[] parts, Runnable ioException)
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
		String cause;
		cause = parts[1];
		MessageDialogUtilities.showErrorMessage(cause);
	}

	public static String getCommand(String cause)
	{
		return TextConverter.merge(new String[]{VERB, cause});
	}
}