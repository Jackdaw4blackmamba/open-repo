import javax.swing.table.*;
import java.util.*;

public class SetColorVisitor extends Visitor
{
	public static final String VERB = "SET_COLOR";

	private ClientFrame frame;

	public SetColorVisitor(String[] parts, Runnable ioException)
	{
		super(parts, ioException);
	}

	public SetColorVisitor(ClientFrame frame, String[] parts, Runnable ioException)
	{
        super(parts, ioException);
        this.frame = frame;
	}

	@Override
	public void visit(CTC ctc)
	{
		String username;
		String buddyName;
		String colorName;
		SetList<Buddy> list;

		username  = parts[1];
		buddyName = parts[2];
		colorName = parts[3];
		list      = ctc.getServer().getBuddyTable().get(username);
		if(list != null)
		{
			for(int i = 0; i < list.size(); i++)
			{
				if(list.get(i).getName().equals(buddyName))
				{
				    list.get(i).setColor(colorName.toUpperCase().charAt(0) + colorName.substring(1));
				    ctc.getServer().saveCurrentData();
				}
			}
		}
	}

	@Override
	public void visit(CTS cts)
	{
		String buddyName;
		String colorName;
		DefaultTableModel tableModel;
		buddyName = parts[1];
		colorName = parts[2];
		tableModel = frame.getTableModel();
		for(int i = 0; i < tableModel.getRowCount(); i++)
			for(int j = 0; j < tableModel.getColumnCount(); j++)
			{
				String val;
				val = (String)tableModel.getValueAt(i, j);
				if(val.equals(buddyName))
				{
					tableModel.setValueAt(colorName.toUpperCase().charAt(0) + colorName.substring(1), i, 2);
					if(frame.getBuddyTable().get(buddyName) != null)
					    frame.getBuddyTable().get(buddyName).setColor(colorName.toUpperCase().charAt(0) + colorName.substring(1));
					break;
				}
			}
	}

	public static String getCommand(String username, String color)
	{
		return TextConverter.merge(new String[]{VERB, username, color});
	}

	public static String getCommand(String username, String buddyName, String color)
	{
		return TextConverter.merge(new String[]{VERB, username, buddyName, color});
	}
}