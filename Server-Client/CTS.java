import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CTS extends Acceptor implements Runnable
{
	private Talker talker;
	private Thread thread;

    private EntryDialog dialog;

    private ClientFrame frame;

    private String username;

	public CTS(Talker talker, EntryDialog dialog)
	{
		this.talker = talker;
		this.dialog = dialog;
		username = dialog.getUsername();
		thread = new Thread(this);
		thread.start();
	}

	public void run()
	{
		Flag flagToQuit;
		flagToQuit = new Flag(false){
		    public void run(){
				MessageDialogUtilities.showErrorMessage("Error: IO exception...");
				up();
			}
		};
		try
		{
			while(!flagToQuit.isUp())
			{
			    String   mergedMsg;
			    String[] parts;
			    String   verb;

			    mergedMsg = talker.receive();
			    parts     = TextConverter.split(mergedMsg);
			    verb      = parts[0];

			    if(verb.equals(LoginDeniedVisitor.VERB))
			        accept(new LoginDeniedVisitor(parts, flagToQuit));//readyToQuit = true

			    else if(verb.equals(RegisterDeniedVisitor.VERB))
			        accept(new RegisterDeniedVisitor(parts, flagToQuit));

				else if(verb.equals(RegisterVisitor.VERB))
				    accept(new RegisterVisitor(parts, flagToQuit));

				else if(verb.equals(LoginVisitor.VERB))
				    accept(new LoginVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestVisitor.VERB))
				    accept(new BuddyRequestVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyNotFoundVisitor.VERB))
				    accept(new BuddyNotFoundVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyAlreadyInListVisitor.VERB))
				    accept(new BuddyAlreadyInListVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyNotOnlineVisitor.VERB))
				    accept(new BuddyNotOnlineVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestReceivedVisitor.VERB))
				    accept(new BuddyRequestReceivedVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestProcrastinatedVisitor.VERB))
				    accept(new BuddyRequestProcrastinatedVisitor(parts, flagToQuit));

				else if(verb.equals(AddBuddyVisitor.VERB))
				    accept(new AddBuddyVisitor(frame, parts, flagToQuit));

				else if(verb.equals(DeleteBuddyVisitor.VERB))
				    accept(new DeleteBuddyVisitor(frame, parts, flagToQuit));

				else if(verb.equals(SetStateOnlineVisitor.VERB))
				    accept(new SetStateOnlineVisitor(frame, parts, flagToQuit));

				else if(verb.equals(SetStateOfflineVisitor.VERB))
				    accept(new SetStateOfflineVisitor(frame, parts, flagToQuit));

				else if(verb.equals(SetStatePendingVisitor.VERB))
				    accept(new SetStatePendingVisitor(frame, parts, flagToQuit));

				else if(verb.equals(SetColorVisitor.VERB))
				    accept(new SetColorVisitor(frame, parts, flagToQuit));

				else if(verb.equals(MessageVisitor.VERB))
				    accept(new MessageVisitor(parts, flagToQuit));

				else if(verb.equals(FileTransferVisitor.VERB))
					accept(new FileTransferVisitor(parts, flagToQuit));

				else if(verb.equals(FileTransferAcceptedVisitor.VERB))
					accept(new FileTransferAcceptedVisitor(parts, flagToQuit));

				else if(verb.equals(FileTransferDeniedVisitor.VERB))
				    accept(new FileTransferDeniedVisitor(parts, flagToQuit));

				else
				{
					if(dialog != null)
					{
						username = dialog.getUsername();
						dialog.dispose();
						dialog = null;
					}
				}
			}
		}
		catch(IOException ioe)
		{
			//MessageDialogUtilities.showErrorMessage("Error: disconnected...");
			MessageDialogUtilities.showModalMessage("Error: disconnected...", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	@Override
	public void accept(Visitor visitor)
	{
		visitor.visit(this);
	}

	public EntryDialog getEntryDialog()
	{
		return dialog;
	}

	public void setEntryDialog(EntryDialog dialog)
	{
		this.dialog = dialog;
	}

	public void setClientFrame(ClientFrame frame)
	{
		this.frame = frame;
	}

	public ClientFrame getClientFrame()
	{
		return frame;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Talker getTalker()
	{
		return talker;
	}

	public String getIPAddressForFileTransfer()
	{
		return "127.0.0.1";
	}

	public int getPortForFileTransfer()
	{
		return 7000;
	}
}