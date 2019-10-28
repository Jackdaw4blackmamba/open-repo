import java.io.*;

public class FileTransferDeniedVisitor extends Visitor
{
	public static final String VERB = "FILE_TRANSFER_DENIED";

	public FileTransferDeniedVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String filename;
		String originatorName;
		String denierName;
		User   originator;

		try
		{
			filename       = parts[1];
			originatorName = parts[2];
			denierName     = parts[3];

			originator = ctc.getServer().getUserTable().get(originatorName);
			if(originator != null && originator.getCTC() != null)
			{
				originator.getCTC().getTalker().send(getCommand(filename, denierName));
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
		String filename;
		String denierName;

		filename   = parts[1];
		denierName = parts[2];

		MessageDialogUtilities.showMessage("\"" + denierName + "\" denied to receive your file \"" + filename + ".\"", "Information");
	}

	public static String getCommand(String filename, String originatorName, String denierName)
	{
		return TextConverter.merge(new String[]{VERB, filename, originatorName, denierName});
	}

	public static String getCommand(String filename, String denierName)
	{
		return TextConverter.merge(new String[]{VERB, filename, denierName});
	}
}