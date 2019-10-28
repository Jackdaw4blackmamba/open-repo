import javax.swing.table.*;

public class SetStateOnlineVisitor extends Visitor
{
	public static final String VERB = "SET_STATE_ONLINE";

	private ClientFrame frame;

	public SetStateOnlineVisitor(ClientFrame frame, String[] parts, Runnable ioException)
	{
		super(parts, ioException);
		this.frame = frame;
	}

	@Override
	public void visit(CTC ctc)
	{
	}

	@Override
	public void visit(CTS cts)
	{
		String buddyName;
		DefaultTableModel tableModel;
		buddyName = parts[1];
		tableModel = frame.getTableModel();
		for(int i = 0; i < tableModel.getRowCount(); i++)
		    for(int j = 0; j < tableModel.getColumnCount(); j++)
		    {
				String val;
				val = (String)tableModel.getValueAt(i, j);
		        if(val.equals(buddyName))
		        {
					HTMLEntity entity;
					HTMLComponent htmlComp;
					HTMLComponent colorComp;
					entity = new HTMLEntity();
					htmlComp = new HTMLComponent(HTMLTag.HTML);
					colorComp = new HTMLComponent(HTMLTag.FONT);
					colorComp.addAttributeSet(new HTMLAttributeSet("color", "green"));
					colorComp.setText("online");
					htmlComp.addChild(colorComp);
					entity.addHTMLComponent(htmlComp);
					tableModel.setValueAt(entity.toString(), i, 0);
					if(frame.getBuddyTable().get(buddyName) != null)
					    frame.getBuddyTable().get(buddyName).setState(Buddy.ONLINE);
					break;
				}
			}
	}

	public static String getCommand(String buddyName)
	{
		return TextConverter.merge(new String[]{VERB, buddyName});
	}
}