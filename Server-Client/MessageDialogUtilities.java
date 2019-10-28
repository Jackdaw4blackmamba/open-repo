import javax.swing.*;

public class MessageDialogUtilities
{
	public static final int REQUEST_ACCEPTED = 0;
	public static final int REQUEST_DENIED   = 1;
	public static final int REQUEST_LATER    = 2;
    public static final int REQUEST_ERROR    = -1;

	public static void showMessage(String msg, String title)
	{
		final String msgToShow   = msg;
		final String dialogTitle = title;
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				JOptionPane.showMessageDialog(null, msgToShow, dialogTitle, JOptionPane.PLAIN_MESSAGE);
			}
		});
	}

	public static void showErrorMessage(String msg)
	{
		final String msgToShow = msg;
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				JOptionPane.showMessageDialog(null, msgToShow, "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public static void showModalMessage(String msg, String title, int messageType)
	{
		JOptionPane.showOptionDialog
		(
			null,
			msg,
			title,
			JOptionPane.OK_OPTION,
			messageType,
			null,
			new String[]{"OK"},
			"OK"
		);
	}

	public static int showBuddyRequestDialog(String requester)
	{
		int result;
		result = JOptionPane.showOptionDialog
		(
			null,
			"\"" + requester + "\" sent you a buddy request.",
			"Buddy Request",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE,
			null,
			new String[]{"Accept", "Deny", "Later"},
			"Later"
		);
		return
			result == 0 ? REQUEST_ACCEPTED :
			result == 1 ? REQUEST_DENIED   :
			result == 2 ? REQUEST_LATER    :
			result == JOptionPane.CLOSED_OPTION ? REQUEST_LATER : REQUEST_ERROR;
	}

}