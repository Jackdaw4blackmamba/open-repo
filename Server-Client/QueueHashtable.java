import java.util.*;
import java.io.*;

public class QueueHashtable
{
	private Hashtable<String, MyQueue<String>> table;

	public QueueHashtable()
	{
		table   = new Hashtable<String, MyQueue<String>>();
	}

	public QueueHashtable(DataInputStream dis) throws IOException
	{
		String key;
		int    size;

		table   = new Hashtable<String, MyQueue<String>>();

		while(dis.available() > 0)
		{
			MyQueue<String> queue;

			queue = new MyQueue<String>();
		    key  = dis.readUTF();
		    size = dis.readInt();
		    for(int i = 0; i < size; i++)
		        queue.add(dis.readUTF());
		    table.put(key, queue);
		}
	}

	public void store(DataOutputStream dos) throws IOException
	{
		List<String> keys;
		keys = Arrays.asList(table.keySet().toArray(new String[]{}));

		for(int i = 0; i < keys.size(); i++)
		{
			MyQueue<String> queue;
			queue = table.get(keys.get(i)).clone();
			dos.writeUTF(keys.get(i));
			dos.writeInt(queue.size());
			while(queue.hasNext())
			    dos.writeUTF(queue.poll());
		}
	}

	public void put(String key, MyQueue<String> queue)
	{
		table.put(key, queue);
	}

	public MyQueue<String> get(String key)
	{
		return table.get(key);
	}

	public Set<String> keySet()
	{
		return table.keySet();
	}
}