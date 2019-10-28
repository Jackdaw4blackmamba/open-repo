import java.awt.*;
import java.io.*;

public class Buddy
{
	public static final int OFFLINE = 0;
	public static final int ONLINE  = 1;
	public static final int PENDING = 2;

	public static final String DEFAULT_COLOR = "green";

	private String  name;
	private int     state;
	private String  colorStr;
	private ChatBox chatBox;

	public Buddy(String name, int state)
	{
		this.name    = name;
		this.state   = state;
		colorStr     = DEFAULT_COLOR;
		chatBox      = null;
	}

	public Buddy(DataInputStream dis) throws IOException
	{
		name = dis.readUTF();
		colorStr = dis.readUTF();
	}

	public void store(DataOutputStream dos) throws IOException
	{
		dos.writeUTF(name);
		dos.writeInt(state);
	}

	public String getName()
	{
		return name;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public int getState()
	{
		return state;
	}

    public void setColor(String colorStr)
    {
		this.colorStr = colorStr;
	}

	public String getColor()
	{
		return colorStr;
	}

	public void setChatBox(ChatBox chatBox)
	{
		this.chatBox = chatBox;
	}

	public ChatBox getChatBox()
	{
		return chatBox;
	}

	@Override
	public boolean equals(Object buddy)
	{
		return getName().equals(((Buddy)buddy).getName());
	}
}