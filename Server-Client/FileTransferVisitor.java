import java.io.*;
import java.net.*;
import javax.swing.*;

public class FileTransferVisitor extends Visitor
{
	public static final String VERB = "FILE_TRANSFER";

	public FileTransferVisitor(String[] parts, Runnable exception)
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
		User   receiver;

		try
		{
			filename     = parts[1];
			fileSize     = parts[2];
			receiverName = parts[3];
		    senderName   = parts[4];

		    receiver = ctc.getServer().getUserTable().get(receiverName);
		    if(receiver != null && receiver.getCTC() != null)
		    {
				receiver.getCTC().getTalker().send(getCommand(filename, Long.parseLong(fileSize), receiverName, senderName));
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
		String       filename;
		String       fileSize;
		String       receiverName;
		String       senderName;
		ServerSocket serverSocket;

		try
		{
			filename     = parts[1];
			fileSize     = parts[2];
			receiverName = parts[3];
			senderName   = parts[4];

			if(JOptionPane.showConfirmDialog(
				null,
				"\"" + senderName + "\" is going to send you a file \"" + filename + "\" (" + fileSize + " bytes). \nAccept?",
				"Confirmation",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
                serverSocket = new ServerSocket(cts.getPortForFileTransfer());

                SwingUtilities.invokeLater(new Runnable(){
					public void run()
					{
						JFileChooser fileChooser;
						Socket       normalSocket;
						fileChooser = new JFileChooser(".");
						fileChooser.setSelectedFile(new File(filename));

						if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
						{
							try
							{
						        cts.getTalker().send(FileTransferAcceptedVisitor.getCommand(
							        filename,
							        Long.parseLong(fileSize),
							        receiverName,
							        senderName,
							        cts.getIPAddressForFileTransfer(),
							        cts.getPortForFileTransfer()
                                ));
                                normalSocket = serverSocket.accept();
                                serverSocket.close();
							    new BabyServer(normalSocket, fileChooser.getSelectedFile().getPath(), Long.parseLong(fileSize));
							}
							catch(IOException ioe)
							{
								MessageDialogUtilities.showErrorMessage("Error: IO exception...");
							}
						}
					}
				});
			}
			else
			{
				cts.getTalker().send(FileTransferDeniedVisitor.getCommand(filename, senderName, receiverName));
			}
		}
		catch(Exception e)
		{
			ioException.run();
		}
	}

	public static String getCommand(String filename, long fileSize, String receiverName, String senderName)
	{
		return TextConverter.merge(new String[]{VERB, filename, "" + fileSize, receiverName, senderName});
	}
}