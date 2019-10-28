import java.io.*;
import java.net.*;

public class FileTransferAcceptedVisitor extends Visitor
{
	public static final String VERB = "FILE_TRANSFER_ACCEPTED";

	public FileTransferAcceptedVisitor(String[] parts, Runnable exception)
	{
		super(parts, exception);
	}

	@Override
	public void visit(CTC ctc)
	{
		String filename;
		String fileSize;
		String receiverName;
		String senderName;
		String ipAddr;
		String port;
		User   sender;

		try
		{
			filename     = parts[1];
			fileSize     = parts[2];
			receiverName = parts[3];
			senderName   = parts[4];
			ipAddr       = parts[5];
			port         = parts[6];

			sender = ctc.getServer().getUserTable().get(senderName);
			if(sender != null && sender.getCTC() != null)
			{
				sender.getCTC().getTalker().send(getCommand(filename, Long.parseLong(fileSize), ipAddr, Integer.parseInt(port)));
			}
		}
		catch(Exception e)
		{
			ioException.run();
		}
	}

	@Override
	public void visit(CTS cts)
	{
		String filename;
		String fileSize;
		String ipAddr;
		String port;
		Socket socket;

		try
		{
			filename = parts[1]; //original filename to send
			fileSize = parts[2];
			ipAddr   = parts[3];
			port     = parts[4];

            socket = new Socket(ipAddr, Integer.parseInt(port));
            new BabyClient(socket, filename, Long.parseLong(fileSize));
		}
		catch(Exception e)
		{
			ioException.run();
		}
	}

	public static String getCommand(String filename, long fileSize, String receiverName, String senderName, String ipAddr, int port)
	{
		return TextConverter.merge(new String[]{VERB, filename, "" + fileSize, receiverName, senderName, ipAddr, "" + port});
	}

	public static String getCommand(String filename, long fileSize, String ipAddr, int port)
	{
		return TextConverter.merge(new String[]{VERB, filename, "" + fileSize, ipAddr, "" + port});
	}
}