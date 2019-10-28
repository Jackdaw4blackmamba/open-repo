import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class ChatScreen extends JPanel implements ComponentListener, DropTargetListener
{
	private JEditorPane  edPane;
	private ChatBox      parentBox;
	private JScrollPane  scrollPane;

	private CTS          cts;

	private DropTarget   dropTarget;
	private File         fileToSend;

	public ChatScreen(ChatBox parentBox, CTS cts)
	{
		setLayout(new BorderLayout());

		edPane = new JEditorPane();
		edPane.setContentType("text/html");
		edPane.setEditable(false);
		edPane.setText("");

		scrollPane = new JScrollPane(edPane);

		add(scrollPane, BorderLayout.CENTER);

		this.parentBox = parentBox;
		parentBox.addComponentListener(this);

		this.cts = cts;

		dropTarget = new DropTarget(edPane, this);
		fileToSend = null;
	}

	public void addMessage(String msg, String color, String alignment)
	{
	    HTMLComponent compToInsert;
	    HTMLComponent childComp;
	    HTMLEntity entity;
	    String baseText;

	    msg = getReplacedText(msg, '\n', "<br>");

	    compToInsert = new HTMLComponent(HTMLTag.DIV);
	    compToInsert.addAttributeSet(new HTMLAttributeSet("align", alignment));
	    childComp = new HTMLComponent(HTMLTag.FONT);
	    childComp.addAttributeSet(new HTMLAttributeSet("color", color));
	    childComp.setText(msg);
	    compToInsert.addChild(childComp);

        baseText = getReplacedText(edPane.getText(), "<br>", '\n');
	    entity = HTMLParser.parse(baseText);
	    entity.insertIntoLastTag(compToInsert, HTMLTag.BODY);
	    edPane.setText(getReplacedText(entity.toString(), '\n', "<br>"));

	    SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				JScrollBar bar = scrollPane.getVerticalScrollBar();
				bar.setValue(bar.getMaximum());
			}
		});
	}

	private String getReplacedText(String baseText, char oldChar, String newStr)
	{
		char[] cs;
		String str;

		cs = baseText.toCharArray();
		str = "";
		for(int i = 0; i < cs.length; i++)
		{
			if(cs[i] == oldChar)
			    str += newStr;
			else
			    str += cs[i];
		}
		return str;
	}

	private String getReplacedText(String baseText, String oldStr, char newChar)
	{
		String[] parts;
		String   str;

		parts = baseText.split(oldStr);
		str = parts[0];
		for(int i = 1; i < parts.length; i++)
		    str += newChar + parts[i];
		return str;
	}

	public void componentHidden(ComponentEvent e){}

	public void componentMoved(ComponentEvent e){}

	public void componentResized(ComponentEvent e)
	{
		setPreferredSize(new Dimension(parentBox.getWidth() * 7 / 8, parentBox.getHeight() * 3 / 5));
	}

	public void componentShown(ComponentEvent e){}

	public void dragEnter(DropTargetDragEvent dtde)
    {
	}

	public void dragExit(DropTargetEvent dte){}

	public void dragOver(DropTargetDragEvent dtde){}

    @SuppressWarnings("unchecked")
	public void drop(DropTargetDropEvent dtde)
	{
		List<File>   list;
		Transferable transferable;

		transferable = dtde.getTransferable();

		try
		{
			if(transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrop(DnDConstants.ACTION_COPY);

				list = (List<File>)(transferable.getTransferData(DataFlavor.javaFileListFlavor));
				if(list != null && list.size() == 1 && list.get(0).isFile())
				{
					fileToSend = list.get(0);
					cts.getTalker().send(FileTransferVisitor.getCommand(
						fileToSend.getName(),
						fileToSend.length(),
						parentBox.getBuddy().getName(),
						parentBox.getUsername()
					));
				}
			}
		}
		catch(UnsupportedFlavorException ufe)
		{
			MessageDialogUtilities.showErrorMessage("Error: unsupported file...");
		}
		catch(IOException ioe)
		{
			MessageDialogUtilities.showErrorMessage("Error: IO exception...");
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde){}
}