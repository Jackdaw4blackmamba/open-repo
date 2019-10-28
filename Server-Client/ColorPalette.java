import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ColorPalette extends JDialog implements ActionListener
{
	public static final int OK_OPTION     = 0;
	public static final int CANCEL_OPTION = 1;

	private JButton[] colorBtns;
	private JLabel    currColorLbl;

	private final Color[]   colors =
	{
		Color.BLACK,      Color.BLUE,    Color.CYAN,
		Color.DARK_GRAY,  Color.GRAY,    Color.GREEN,
		Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
		Color.PINK,       Color.RED,     Color.WHITE,
		Color.YELLOW
	};
	private Color currColor;

	private int   currOption;

	public ColorPalette(Color currColor)
	{
		this.currColor = currColor;
		currOption     = CANCEL_OPTION;
	}

	public void showPalette()
	{
		JPanel  mainPnl;
		JPanel  tmpPnl;
		JPanel  buttonPnl;
		JButton okBtn;
		JButton cancelBtn;
		String  colorStr;

		mainPnl = new JPanel(new GridLayout(2, (colors.length + 1) / 2));

		tmpPnl = new JPanel(new BorderLayout());
		colorStr = getColorName(currColor);
		currColorLbl = new JLabel(getLabelText(colorStr.toUpperCase().charAt(0) + colorStr.substring(1)));
		tmpPnl.add(currColorLbl, BorderLayout.CENTER);
		mainPnl.add(tmpPnl);

        colorBtns = new JButton[colors.length];
		for(int i = 0; i < colors.length; i++)
		{
			colorBtns[i] = new JButton();
			colorBtns[i].setBackground(colors[i]);
			colorBtns[i].setActionCommand("SELECT_COLOR");
			colorBtns[i].addActionListener(this);
			mainPnl.add(colorBtns[i]);
		}

		this.add(mainPnl, BorderLayout.CENTER);

		buttonPnl = new JPanel();
		okBtn = new JButton("OK");
		okBtn.setActionCommand("OK");
		okBtn.addActionListener(this);
		buttonPnl.add(okBtn);
		cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand("CANCEL");
		cancelBtn.addActionListener(this);
		buttonPnl.add(cancelBtn);

		this.add(buttonPnl, BorderLayout.SOUTH);

	    setupMainDialog(20, 10, "Color Palette");
	}

	private void setupMainDialog(int xScreenPercentage, int yScreenPercentage, String title)
	{
		Toolkit   tk;
		Dimension d;

	    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

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

		if(cmd.equals("SELECT_COLOR"))
		{
			//Color  selectedColor;
			String colorStr;
			//selectedColor = ((JButton)e.getSource()).getBackground();
			for(int i = 0; i < colorBtns.length; i++)
			    if(colorBtns[i].equals((JButton)e.getSource()))
			    {
					currColor = colors[i];
					break;
				}
			//currColor = selectedColor;
			colorStr = getColorName(currColor);
			currColorLbl.setText(getLabelText(colorStr.toUpperCase().charAt(0) + colorStr.substring(1)));
		}
		else if(cmd.equals("OK"))
		{
			currOption = OK_OPTION;
			this.dispose();
		}
		else if(cmd.equals("CANCEL"))
		{
			currOption = CANCEL_OPTION;
			this.dispose();
		}
	}

	public static String getColorName(Color color)
	{
		return
		    color == Color.BLACK      ? "black"     :
		    color == Color.BLUE       ? "blue"      :
		    color == Color.CYAN       ? "cyan"      :
		    color == Color.DARK_GRAY  ? "darkgray"  :
		    color == Color.GRAY       ? "gray"      :
		    color == Color.GREEN      ? "green"     :
		    color == Color.LIGHT_GRAY ? "lightgray" :
		    color == Color.MAGENTA    ? "magenta"   :
		    color == Color.ORANGE     ? "orange"    :
		    color == Color.PINK       ? "pink"      :
		    color == Color.RED        ? "red"       :
		    color == Color.WHITE      ? "white"     :
		    color == Color.YELLOW     ? "yellow"    : "undefined";
	}

	public static Color getColor(String colorStr)
	{
		colorStr = colorStr.toLowerCase();
		return
		    colorStr.equals("black")     ? Color.BLACK      :
		    colorStr.equals("blue")      ? Color.BLUE       :
		    colorStr.equals("cyan")      ? Color.CYAN       :
		    colorStr.equals("darkgray")  ? Color.DARK_GRAY  :
		    colorStr.equals("gray")      ? Color.GRAY       :
		    colorStr.equals("green")     ? Color.GREEN      :
		    colorStr.equals("lightgray") ? Color.LIGHT_GRAY :
		    colorStr.equals("magenta")   ? Color.MAGENTA    :
		    colorStr.equals("oraqnge")   ? Color.ORANGE     :
		    colorStr.equals("pink")      ? Color.PINK       :
		    colorStr.equals("red")       ? Color.RED        :
		    colorStr.equals("white")     ? Color.WHITE      :
		    colorStr.equals("yellow")    ? Color.YELLOW     : Color.BLACK;
	}

	private String getLabelText(String colorName)
	{
		return "<html>" + "Selected:" + "<br>" + colorName + "</html>";
	}

	public Color getSelectedColor()
	{
		return currColor;
	}

	public String getSelectedColorAsString()
	{
		return getColorName(currColor);
	}

	public int getSelectedOption()
	{
		return currOption;
	}
}