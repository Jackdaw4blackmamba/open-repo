import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class EntryDialog extends JDialog implements ActionListener, DocumentListener, ChangeListener
{
	private JTextField     usernameTF;
	private JPasswordField passwordPF;
	private JCheckBox      showPasswdCB;
	private JButton        registerBtn;
	private JButton        loginBtn;

	private CTS            cts;
	private Talker         talker;
	private String         username;

	public EntryDialog()
	{
		cts      = null;
		talker   = null;
		username = null;
	}

	public void showDialog()
	{
		JPanel mainPnl;
		JPanel tmpPnl;
		JPanel paddPnl;
	    JLabel tmpLbl;

		mainPnl = new JPanel(new GridLayout(4, 1));
		mainPnl.setBorder(new EmptyBorder(10, 10, 10, 10));

		tmpPnl = new JPanel(new BorderLayout());
		tmpPnl.setBorder(new EmptyBorder(0, 0, 10, 0));
		paddPnl = new JPanel(new BorderLayout());
		paddPnl.setBorder(new EmptyBorder(0, 10, 0, 0));
		tmpLbl = new JLabel("Username");
		usernameTF = new JTextField();
		usernameTF.setBorder(new LineBorder(Color.GRAY));
		usernameTF.getDocument().addDocumentListener(this);
		paddPnl.add(usernameTF, BorderLayout.CENTER);
		tmpPnl.add(tmpLbl, BorderLayout.WEST);
		tmpPnl.add(paddPnl, BorderLayout.CENTER);
		mainPnl.add(tmpPnl);

		tmpPnl = new JPanel(new BorderLayout());
		tmpPnl.setBorder(new EmptyBorder(0, 0, 10, 0));
		paddPnl = new JPanel(new BorderLayout());
		paddPnl.setBorder(new EmptyBorder(0, 10, 0, 0));
		tmpLbl = new JLabel("Password");
		passwordPF = new JPasswordField();
		passwordPF.setBorder(new LineBorder(Color.GRAY));
		passwordPF.getDocument().addDocumentListener(this);
		paddPnl.add(passwordPF, BorderLayout.CENTER);
		tmpPnl.add(tmpLbl, BorderLayout.WEST);
		tmpPnl.add(paddPnl, BorderLayout.CENTER);
		mainPnl.add(tmpPnl);

		tmpPnl = new JPanel();
		tmpPnl.setBorder(new EmptyBorder(0, 0, 10, 0));
		showPasswdCB = new JCheckBox("Show password");
		showPasswdCB.addChangeListener(this);
		tmpPnl.add(showPasswdCB);
		mainPnl.add(tmpPnl);

		tmpPnl = new JPanel();
		registerBtn = new JButton("Register");
		registerBtn.setActionCommand("REGISTER");
		registerBtn.addActionListener(this);
		registerBtn.setEnabled(false);
		loginBtn = new JButton("Login");
		loginBtn.setActionCommand("LOGIN");
		loginBtn.addActionListener(this);
		loginBtn.setEnabled(false);
		tmpPnl.add(registerBtn);
		tmpPnl.add(loginBtn);
		mainPnl.add(tmpPnl);

		add(mainPnl, BorderLayout.CENTER);

		setupMainDialog(15, 15, "Entry Dialog");
	}

	private void setupMainDialog(int xScreenPercentage, int yScreenPercentage, String title)
	{
		Toolkit   tk;
		Dimension d;

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		tk = Toolkit.getDefaultToolkit();
		d  = tk.getScreenSize();
		setSize(xScreenPercentage * d.width / 100, yScreenPercentage * d.height / 100);
		setLocation((100 - xScreenPercentage) * d.width / 200, (100 - yScreenPercentage) * d.height / 200);
		setTitle(title);
		setVisible(true);
	}

    public void actionPerformed(ActionEvent e)
    {
		String cmd;
		cmd = e.getActionCommand();

		if(cmd.equals("REGISTER"))
		{
			String username;

            try
            {
				username = usernameTF.getText().trim();
			    talker = new Talker("127.0.0.1", 6789, username);
			    cts = new CTS(talker, this);

                talker.send(RegisterVisitor.getCommand(username, new String(passwordPF.getPassword())));
		    }
		    catch(Exception ee)
		    {
				MessageDialogUtilities.showErrorMessage("Error: exception...");
			}

            usernameTF.setText("");
            passwordPF.setText("");
		}
		else if(cmd.equals("LOGIN"))
		{
			String username;

            try
            {
				username = usernameTF.getText().trim();
				if(talker == null)
				{
			        talker = new Talker("127.0.0.1", 6789, username);
			        cts = new CTS(talker, this);
			    }

                talker.send(LoginVisitor.getCommand(username, new String(passwordPF.getPassword())));
			}
			catch(Exception ee)
			{
				MessageDialogUtilities.showErrorMessage("Error: exception...");
			}

            usernameTF.setText("");
            passwordPF.setText("");
	    }
	}

	public void changedUpdate(DocumentEvent e) {}

	public void insertUpdate(DocumentEvent e)
	{
		update();
	}

	public void removeUpdate(DocumentEvent e)
	{
		update();
	}

	public void stateChanged(ChangeEvent e)
	{
		JCheckBox cb;
		cb = (JCheckBox)e.getSource();
		passwordPF.setEchoChar(cb.isSelected() ? 0 : new JPasswordField().getEchoChar());
	}

	private void update()
	{
		boolean valid;
		valid = isReady();
		registerBtn.setEnabled(valid);
		loginBtn.setEnabled(valid);
	}

	private boolean isReady()
	{
		String usernameTxt;
		char[] passwdTxt;

		usernameTxt = usernameTF.getText().trim();
		passwdTxt = passwordPF.getPassword();

		if(!usernameTxt.equals("") && passwdTxt.length > 0)
		    return true;
		return false;
	}

	public CTS getCTS()
	{
		return cts;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return username;
	}
}