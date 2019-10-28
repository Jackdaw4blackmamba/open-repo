import java.io.*;
import java.util.*;
import javax.swing.*;

public class MessageVisitor extends Visitor
{
	public static final String VERB = "MESSAGE";

	public MessageVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	@Override
	public void visit(CTC ctc)
	{
		String msg;
		String receiverName;
		String senderName;
		User   receiver;

		try
		{
			msg = parts[1];
			receiverName = parts[2];
			senderName = parts[3];
		    receiver = ctc.getServer().getUserTable().get(receiverName);
		    if(receiver != null)
		    {
			    if(receiver.getCTC() != null)
			        receiver.getCTC().getTalker().send(getCommand(msg, senderName));
			    else
			        ;// waiting list
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
		String msg;
		String senderName;
		Buddy  sender;

		msg = new String(Base64.getDecoder().decode(parts[1]));
		senderName = parts[2];
		sender = cts.getClientFrame().getBuddyTable().get(senderName);
		if(sender != null)
		{
			if(sender.getChatBox() != null)
			    sender.getChatBox().getChatScreen().addMessage(msg, sender.getColor(), "left");
			else
			{
				if(JOptionPane.showConfirmDialog(null, senderName + " is trying to chat with you.\nAccept?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					ChatBox chatBox;
					chatBox = new ChatBox(cts, cts.getUsername(), sender, cts.getClientFrame());
					chatBox.toFront();
					sender.setChatBox(chatBox);
					chatBox.getChatScreen().addMessage(msg, sender.getColor(), "left");
				}
			}
		}
	}

	public static String getCommand(String msg, String receiver, String sender)
	{
		return TextConverter.merge(new String[]{VERB, Base64.getEncoder().encodeToString(msg.getBytes()), receiver, sender});
	}

	private String getCommand(String msg, String sender)
	{
		return TextConverter.merge(new String[]{VERB, msg, sender});
	}
}