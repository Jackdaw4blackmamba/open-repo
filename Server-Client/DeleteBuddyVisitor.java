import javax.swing.table.*;

public class DeleteBuddyVisitor extends Visitor
{
	public static final String VERB = "DELETE_BUDDY";

	private ClientFrame frame;

	public DeleteBuddyVisitor(ClientFrame frame, String[] parts, Runnable ioException)
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
					tableModel.removeRow(i);
					return;
				}
			}
	}

	public static String getCommand(String buddyToDelete)
	{
		return TextConverter.merge(new String[]{VERB, buddyToDelete});
	}
}