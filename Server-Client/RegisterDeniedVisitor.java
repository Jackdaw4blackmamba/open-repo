import java.util.*;
import java.io.*;

public class RegisterDeniedVisitor extends Visitor
{
	public static final String VERB = "REGISTER_DENIED";

	public RegisterDeniedVisitor(String[] parts, Runnable ioException)
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