import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;

public class ChatBox extends JDialog implements ActionListener, WindowListener
{
	private ChatScreen screen;
	private JLabel     buddyPenColorLbl;
	private JButton    buddyPenColorBtn;
	private JLabel     penColorLbl;
	private JButton    penColorBtn;
	private JTextArea  msgArea;
	private JButton    sendBtn;

	private CTS    cts;
	private String username;
	private String penColor;
	private Buddy  buddy;

	private ClientFrame frame;

	public ChatBox(CTS cts, String username, Buddy buddy, ClientFrame frame)
	{
		JPanel mainPnl;
		JPanel midPnl;
		JPanel tmpPnl;
		JPanel penColorPnl;
		JPanel buttonPnl;

		this.cts      = cts;
		this.username = username;
		this.frame    = frame;
		penColor      = frame.getPenColor();
		this.buddy    = buddy;

		mainPnl = new JPanel(new GridLayout(2,1));
		mainPnl.setBorder(new EmptyBorder(10, 10, 10, 10));

		screen = new ChatScreen(this, cts);
		screen.setBorder(new EmptyBorder(0, 0, 10, 0));
		mainPnl.add(screen);

		midPnl = new JPanel(new BorderLayout());

		msgArea = new JTextArea();
		msgArea.setBorder(new LineBorder(Color.GRAY));
		msgArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK), "BR");
		msgArea.getActionMap().put("BR", new javax.swing.text.DefaultEditorKit.InsertBreakAction());
		msgArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "NO_BR");
		msgArea.getActionMap().put("NO_BR", null);
		midPnl.add(msgArea, BorderLayout.CENTER);

		tmpPnl = new JPanel(new GridLayout(2,1));

        penColorPnl = new JPanel();
		buddyPenColorLbl = new JLabel("Buddy's pen color: " + buddy.getColor().toUpperCase().charAt(0) + buddy.getColor().substring(1));
		buddyPenColorBtn = new JButton();
		buddyPenColorBtn.setBackground(ColorPalette.getColor(buddy.getColor()));
		buddyPenColorBtn.setActionCommand("BUDDY_PEN_COLOR");
		buddyPenColorBtn.addActionListener(this);
		penColorLbl = new JLabel("Your pen color: " + penColor.toUpperCase().charAt(0) + penColor.substring(1));
		penColorBtn = new JButton();
		penColorBtn.setBackground(ColorPalette.getColor(penColor));
		penColorBtn.setActionCommand("PEN_COLOR");
		penColorBtn.addActionListener(this);
		penColorPnl.add(buddyPenColorLbl);
		penColorPnl.add(buddyPenColorBtn);
		penColorPnl.add(penColorLbl);
		penColorPnl.add(penColorBtn);
		tmpPnl.add(penColorPnl);

		buttonPnl = new JPanel();
		buttonPnl.setBorder(new EmptyBorder(0, 0, 10, 0));
		sendBtn = new JButton("Send");
		sendBtn.setActionCommand("SEND");
		sendBtn.addActionListener(this);
		this.getRootPane().setDefaultButton(sendBtn);
		buttonPnl.add(sendBtn);
		tmpPnl.add(buttonPnl, BorderLayout.SOUTH);

		midPnl.add(tmpPnl, BorderLayout.SOUTH);

		mainPnl.add(midPnl);

		this.add(mainPnl, BorderLayout.CENTER);

		setupMainDialog(30, 50, "ChatBox: " + buddy.getName());
	}

	private void setupMainDialog(int xScreenPercentage, int yScreenPercentage, String title)
	{
		Toolkit   tk;
		Dimension d;

	    this.addWindowListener(this);
	    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    this.setModalityType(Dialog.ModalityType.MODELESS);

		tk = Toolkit.getDefaultToolkit();
		d  = tk.getScreenSize();
		this.setSize(xScreenPercentage * d.width / 100, yScreenPercentage * d.height / 100);
		this.setLocation((100 - xScreenPercentage) * d.width / 200, (100 - yScreenPercentage) * d.height / 200);
		this.setTitle(title);
	    this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd;
		cmd = e.getActionCommand();

		if(cmd.equals("SEND"))
		{
			String msg;
			msg = msgArea.getText();

			try
			{
				cts.getTalker().send(MessageVisitor.getCommand(msg, buddy.getName(), username));
			    screen.addMessage(msg, penColor, "right");
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: failed to send message...");
			}

			msgArea.setText("");
		}
		else if(cmd.equals("PEN_COLOR"))
		{
			ColorPalette palette;
			palette = new ColorPalette(ColorPalette.getColor(penColor));
			palette.showPalette();
			if(palette.getSelectedOption() == ColorPalette.OK_OPTION)
			{
				String penColStr;
				penColStr = palette.getSelectedColorAsString();
				penColorBtn.setBackground(palette.getSelectedColor());
				penColorLbl.setText("Pen color: " + penColStr.toUpperCase().charAt(0) + penColStr.substring(1));
				penColor = penColStr;
				frame.setPenColor(penColor);
			}
		}
		else if(cmd.equals("BUDDY_PEN_COLOR"))
		{
			ColorPalette palette;
			palette = new ColorPalette(ColorPalette.getColor(buddy.getColor()));
			palette.showPalette();
			if(palette.getSelectedOption() == ColorPalette.OK_OPTION)
			{
				String penColStr;
				penColStr = palette.getSelectedColorAsString();
				buddyPenColorBtn.setBackground(palette.getSelectedColor());
				buddyPenColorLbl.setText("Buddy's pen color: " + penColStr.toUpperCase().charAt(0) + penColStr.substring(1));
				buddy.setColor(penColStr);
				try
				{
					cts.getTalker().send(SetColorVisitor.getCommand(username, buddy.getName(), penColStr));
				}
				catch(IOException ioe)
				{
					MessageDialogUtilities.showErrorMessage("Error: IO exception...");
				}
			}
		}
	}

	public void windowActivated(WindowEvent e){}

	public void windowClosed(WindowEvent e){}

	public void windowClosing(WindowEvent e)
	{
		buddy.setChatBox(null);
		dispose();
	}

	public void windowDeactivated(WindowEvent e){}

	public void windowDeiconified(WindowEvent e){}

	public void windowIconified(WindowEvent e){}

	public void windowOpened(WindowEvent e){}

	public ChatScreen getChatScreen()
	{
		return screen;
	}

	public Buddy getBuddy()
	{
		return buddy;
	}

	public String getUsername()
	{
		return username;
	}
}