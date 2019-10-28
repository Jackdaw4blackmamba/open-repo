import javax.swing.table.*;

public class AddBuddyVisitor extends Visitor
{
	public static final String VERB = "ADD_BUDDY";

	private ClientFrame frame;

	public AddBuddyVisitor(ClientFrame frame, String[] parts, Runnable ioException)
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
		buddyName = parts[1];
		frame.getTableModel().addRow(new String[]{"", buddyName, ""});
		frame.getBuddyTable().put(buddyName, new Buddy(buddyName, Buddy.OFFLINE));
	}

	public static String getCommand(String buddy)
	{
		return TextConverter.merge(new String[]{VERB, buddy});
	}
}