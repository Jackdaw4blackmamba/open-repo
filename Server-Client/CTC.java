import java.io.*;
import java.util.*;

public class CTC extends Acceptor implements Runnable
{
	private Talker talker;
	private Thread thread;

	private Server server;

	public CTC(Talker talker, Server server)
	{
		this.talker = talker;
		this.server = server;
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

				if(verb.equals(RegisterVisitor.VERB))
				    accept(new RegisterVisitor(parts, flagToQuit));

				else if(verb.equals(LoginVisitor.VERB))
				    accept(new LoginVisitor(parts, flagToQuit));

				else if(verb.equals(ClientViewConstructedVisitor.VERB))
				    accept(new ClientViewConstructedVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestVisitor.VERB))
				    accept(new BuddyRequestVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestAcceptedVisitor.VERB))
				    accept(new BuddyRequestAcceptedVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestDeniedVisitor.VERB))
				    accept(new BuddyRequestDeniedVisitor(parts, flagToQuit));

				else if(verb.equals(BuddyRequestProcrastinatedVisitor.VERB))
				    accept(new BuddyRequestProcrastinatedVisitor(parts, flagToQuit));

				else if(verb.equals(SetColorVisitor.VERB))
				    accept(new SetColorVisitor(parts, flagToQuit));

				else if(verb.equals(MessageVisitor.VERB))
				    accept(new MessageVisitor(parts, flagToQuit));

				else if(verb.equals(UnfriendVisitor.VERB))
				    accept(new UnfriendVisitor(parts, flagToQuit));

				else if(verb.equals(FileTransferVisitor.VERB))
				    accept(new FileTransferVisitor(parts, flagToQuit));

				else if(verb.equals(FileTransferAcceptedVisitor.VERB))
				    accept(new FileTransferAcceptedVisitor(parts, flagToQuit));

				else if(verb.equals(FileTransferDeniedVisitor.VERB))
				    accept(new FileTransferDeniedVisitor(parts, flagToQuit));

				else if(verb.equals(LogoffVisitor.VERB))
				    accept(new LogoffVisitor(parts, flagToQuit));
			}
		}
		catch(IOException ioe)
		{
			Hashtable<String, CTC> ctcTable;
			List<String>           keys;
			ctcTable = server.getCTCTable();
			keys     = Arrays.asList(ctcTable.keySet().toArray(new String[]{}));

			for(String key : keys)
			    if(this.equals(ctcTable.get(key)))
			    {
					ctcTable.remove(key);
					break;
				}
		}
	}

	@Override
	public void accept(Visitor visitor)
	{
		visitor.visit(this);
	}

	public Server getServer()
	{
		return server;
	}

	public Talker getTalker()
	{
		return talker;
	}

	public String getID()
	{
		return talker.getID();
	}
}