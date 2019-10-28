import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.net.*;
import java.io.*;

public class Client
{
	public static void main(String[] args)
	{
		EntryDialog dialog;

        dialog = new EntryDialog();
        dialog.showDialog();
		new ClientFrame(dialog.getCTS(), dialog.getUsername());
	}
}

class ClientFrame extends JFrame implements ActionListener, MouseListener, WindowListener
{
	private JLabel        stateLbl;
	private JButton       connectBtn;
	private JButton       addBuddyBtn;
	private JButton       chatBtn;
	private JButton       exitBtn;
	private JTable        buddyTableBox;

    private Talker        talker;
	private CTS           cts;
	private String        username;
	private String        penColor;

	private DefaultTableModel tableModel;

	private Hashtable<String, Buddy> buddyTable;
	private Color foreColor;

	public ClientFrame(CTS cts, String username)
	{
		this.cts = cts;
		this.username = username;
		penColor = "blue";
		talker = null;
		if(cts != null)
		    talker = cts.getTalker();
		buddyTable = new Hashtable<String, Buddy>();
		foreColor = Color.BLUE;

		constructGUI();

		if(cts != null)
		    cts.setClientFrame(this);
		if(cts == null || username == null)
		{
			stateLbl.setForeground(Color.RED);
			stateLbl.setText("You are not logged in.");
		}
		else
		    stateLbl.setText("Logged in as " + username);

        try
        {
		    if(talker != null && username != null)
		        talker.send(ClientViewConstructedVisitor.getCommand(username));
		}
		catch(IOException ioe)
		{
			MessageDialogUtilities.showErrorMessage("Error: failed to connect...");
		}
	}

	private void constructGUI()
	{
		JPanel      mainPnl;
		JPanel      statePnl;
		JPanel      buttonPnl;
		JScrollPane scrollPane;

		mainPnl = new JPanel(new BorderLayout());

        statePnl = new JPanel(new BorderLayout());
        statePnl.setBorder(new EmptyBorder(5, 5, 5, 5));
        statePnl.add(new JLabel("State:"), BorderLayout.WEST);
		stateLbl = new JLabel();
		stateLbl.setBorder(new EmptyBorder(0, 10, 0, 0));
		statePnl.add(stateLbl, BorderLayout.CENTER);
		mainPnl.add(statePnl, BorderLayout.NORTH);

		tableModel = new DefaultTableModel();
		tableModel.addColumn("State");
		tableModel.addColumn("Username");
		tableModel.addColumn("Fore color");
		buddyTableBox = new JTable(tableModel);
		buddyTableBox.addMouseListener(this);
		buddyTableBox.setDefaultEditor(Object.class, null);
		scrollPane = new JScrollPane(buddyTableBox);
		mainPnl.add(scrollPane, BorderLayout.CENTER);

		buttonPnl = new JPanel();
		connectBtn = new JButton("Connect");
		connectBtn.setActionCommand("CONNECT");
		connectBtn.addActionListener(this);
		connectBtn.setEnabled(username == null);
		addBuddyBtn = new JButton("Add buddy");
		addBuddyBtn.setActionCommand("ADD_BUDDY");
		addBuddyBtn.addActionListener(this);
		chatBtn = new JButton("Start chat");
		chatBtn.setActionCommand("CHAT");
		chatBtn.addActionListener(this);
		exitBtn = new JButton("Exit");
		exitBtn.setActionCommand("EXIT");
		exitBtn.addActionListener(this);
		buttonPnl.add(connectBtn);
		buttonPnl.add(addBuddyBtn);
		buttonPnl.add(chatBtn);
		buttonPnl.add(exitBtn);
		mainPnl.add(buttonPnl, BorderLayout.SOUTH);

		this.add(mainPnl, BorderLayout.CENTER);

		//this.getRootPane().setDefaultButton(sendBtn);

		setupMainFrame(15, 20, "Client");
	}

	private void setupMainFrame(int xScreenPercentage, int yScreenPercentage, String title)
	{
		Toolkit   tk;
		Dimension d;

        this.addWindowListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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

		if(cmd.equals("CONNECT"))
		{
			EntryDialog dialog;
			try
			{
			    dialog = new EntryDialog();
			    dialog.showDialog();
			    cts = dialog.getCTS();
			    username = dialog.getUsername();
			    if(cts != null && username != null)
			    {
			        talker = cts.getTalker();
			        cts.setClientFrame(this);
			        stateLbl.setText("Logged in as " + username);
			        stateLbl.setForeground(Color.BLACK);
			        connectBtn.setEnabled(true);
			        talker.send(ClientViewConstructedVisitor.getCommand(username));
			    }
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: failed to connect...");
			}
		}
		else if(cmd.equals("ADD_BUDDY"))
		{
			String buddyName;
			buddyName = showBuddyNameInputDialog();
			if(buddyName != null)
			{
				buddyName = buddyName.trim();
				if(!buddyName.equals(username))
				{
				    try
				    {
						talker.send(BuddyRequestVisitor.getCommand(buddyName, username));
				    }
				    catch(IOException ioe)
				    {
				    	JOptionPane.showMessageDialog(null, "Error: failed to send request...");
				    }
				}
				else
                    JOptionPane.showMessageDialog(null, "\"" + buddyName + "\" is the same to yours.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(cmd.equals("EXIT"))
		{
			if(JOptionPane.showConfirmDialog(null, "Do you really want to exit?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
			    closeDialog();
		}
		else if(cmd.equals("ACCEPT"))
		{
			Buddy buddy;
			try
			{
				buddy = getBuddyFromTableBox();
				if(buddy != null)
				    cts.getTalker().send(BuddyRequestAcceptedVisitor.getCommand(username, buddy.getName()));
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: IO exception...");
			}
		}
		else if(cmd.equals("DENY"))
		{
			Buddy buddy;
			try
			{
				buddy = getBuddyFromTableBox();
				if(buddy != null)
				    cts.getTalker().send(BuddyRequestDeniedVisitor.getCommand(buddy.getName(), username));
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: IO exception...");
			}
		}
		else if(cmd.equals("CHAT"))
		{
			showChatBox();
		}
		else if(cmd.equals("SELECT_COLOR"))
		{
			Buddy        buddy;
			ColorPalette palette;
			String       colStr;

            try
            {
			    buddy = getBuddyFromTableBox();
			    palette = new ColorPalette(ColorPalette.getColor(buddy.getColor()));
			    palette.showPalette();
			    if(palette.getSelectedOption() == ColorPalette.OK_OPTION)
			    {
				    for(int i = 0; i < buddyTableBox.getRowCount(); i++)
				        for(int j = 0; j < buddyTableBox.getColumnCount(); j++)
				            if(((String)tableModel.getValueAt(i, j)).equals(buddy.getName()))
				            {
							    tableModel.setValueAt(palette.getSelectedColorAsString(), i, 2);
							    break;
						    }
			    }
			    colStr = palette.getSelectedColorAsString();
			    cts.getTalker().send(SetColorVisitor.getCommand(username, buddy.getName(), colStr.toUpperCase().charAt(0) + colStr.substring(1)));
			}
			catch(IOException ioe)
			{
				MessageDialogUtilities.showErrorMessage("Error: IO exception...");
			}
		}
		else if(cmd.equals("UNFRIEND"))
		{
			Buddy buddy;

			try
			{
			    buddy = getBuddyFromTableBox();
			    if(buddy != null)
			    {
			        if(JOptionPane.showConfirmDialog(null, "Do you really want to unfriend " + buddy.getName() + "?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
			            cts.getTalker().send(UnfriendVisitor.getCommand(username, buddy.getName()));
		        }
			}
			catch(IOException ioe)
			{
                MessageDialogUtilities.showErrorMessage("Error: IO exception...");
			}
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		chatBtn.setEnabled(getBuddyFromTableBox() != null);

		if(SwingUtilities.isRightMouseButton(e))
		{
			int row;

            row = -1;
            for(int i = 0; i < buddyTableBox.getRowCount(); i++)
                for(int j = 0; j < buddyTableBox.getColumnCount(); j++)
                    if(buddyTableBox.getCellRect(i, j, false).contains(e.getX(), e.getY()))
                    {
						row = i;
						break;
					}

			if(0 <= row && row < buddyTableBox.getRowCount())
			{
				String buddyName;
				Buddy  buddy;

				buddyName = (String)tableModel.getValueAt(row, 1);
				buddy = buddyTable.get(buddyName);
				buddyTableBox.setRowSelectionInterval(row, row);//select
				if(buddy != null)
				{
					JPopupMenu popup;
					JMenuItem  item;

					popup = new JPopupMenu();

					if(buddy.getState() == Buddy.PENDING)
					{
						item = new JMenuItem("Accept");
						item.setActionCommand("ACCEPT");
						item.addActionListener(this);
						popup.add(item);

						item = new JMenuItem("Deny");
						item.setActionCommand("DENY");
						item.addActionListener(this);
						popup.add(item);
					}
					else
					{
						item = new JMenuItem("Start chat");
						item.setActionCommand("CHAT");
						item.addActionListener(this);
						popup.add(item);

						item = new JMenuItem("Select color");
						item.setActionCommand("SELECT_COLOR");
						item.addActionListener(this);
						popup.add(item);

						item = new JMenuItem("Unfriend");
						item.setActionCommand("UNFRIEND");
						item.addActionListener(this);
						popup.add(item);
					}
					popup.show(buddyTableBox, e.getX(), e.getY());
				}
			}
		}
		else if(e.getClickCount() >= 2)
		{
			showChatBox();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		chatBtn.setEnabled(getBuddyFromTableBox() != null);
	}

	public void mouseExited(MouseEvent e)
	{
		chatBtn.setEnabled(getBuddyFromTableBox() != null);
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void windowActivated(WindowEvent e){}

	public void windowClosed(WindowEvent e){}

	public void windowClosing(WindowEvent e)
	{
		if(JOptionPane.showConfirmDialog(null, "Do you really want to exit?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
		    closeDialog();
	}

	public void windowDeactivated(WindowEvent e){}

	public void windowDeiconified(WindowEvent e){}

	public void windowIconified(WindowEvent e){}

	public void windowOpened(WindowEvent e){}

    public void setPenColor(String penColor)
    {
		this.penColor = penColor;
	}

	public String getPenColor()
	{
		return penColor;
	}

	public DefaultTableModel getTableModel()
	{
		return tableModel;
	}

	public Hashtable<String, Buddy> getBuddyTable()
	{
		return buddyTable;
	}

	public Color getForeColor()
	{
		return foreColor;
	}

	private Buddy getBuddyFromTableBox()
	{
		Buddy buddy;
		int   row;

		buddy = null;
		row = buddyTableBox.getSelectedRow();
		if(0 <= row && row < buddyTableBox.getRowCount())
		{
			String buddyName;
			buddyName = (String)tableModel.getValueAt(row, 1);
			buddy = buddyTable.get(buddyName);
		}

		return buddy;
	}

	private void showChatBox()
	{
		Buddy buddy;

		buddy = getBuddyFromTableBox();
		if(buddy != null)
		{
			if(buddy.getState() == Buddy.ONLINE)
			{
			    if(buddy.getChatBox() != null)
				    buddy.getChatBox().toFront();
			    else
			    {
				    buddy.setChatBox(new ChatBox(cts, username, buddy, this));
				    buddy.getChatBox().repaint();
				}
			}
			else if(buddy.getState() == Buddy.OFFLINE)
			    MessageDialogUtilities.showMessage(buddy.getName() + " is currently not online.", "Information");
			else if(buddy.getState() == Buddy.PENDING)
			    MessageDialogUtilities.showMessage(buddy.getName() + " is not a buddy of yours.", "Information");
		}
	}

	private String showBuddyNameInputDialog()
	{
		String buddyName;

		buddyName = JOptionPane.showInputDialog(null, "Enter your buddy's name", "Buddy Request Dialog", JOptionPane.PLAIN_MESSAGE);

		if(buddyName != null && buddyName.trim().equals(""))
		{
			MessageDialogUtilities.showErrorMessage("Error: nothing entered...");
		    buddyName = null;
		}

		return buddyName;
	}

	private void closeDialog()
	{
		try
		{
			if(username != null)
		        talker.send(LogoffVisitor.getCommand(username));
		    System.exit(0);
		}
		catch(IOException ioe)
		{
			if(JOptionPane.showConfirmDialog(null, "Error: I/O exception...\nExit anyway?", "Error", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
			{
				System.exit(0);
			}
		}
	}
}