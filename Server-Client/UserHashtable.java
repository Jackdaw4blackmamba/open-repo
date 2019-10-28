import java.util.*;
import java.io.*;

public class UserHashtable
{
	private Hashtable<String, User> table;

	public UserHashtable()
	{
		table   = new Hashtable<String, User>();
	}

	public UserHashtable(DataInputStream dis) throws IOException
	{
		table   = new Hashtable<String, User>();

        while(dis.available() > 0)
			table.put(dis.readUTF(), new User(dis));
	}

	public void store(DataOutputStream dos) throws IOException
	{
		List<String> keys;
		keys = Arrays.asList(table.keySet().toArray(new String[]{}));

        for(int i = 0; i < keys.size(); i++)
        {
			dos.writeUTF(keys.get(i));
			table.get(keys.get(i)).store(dos);
		}
	}

	public void put(String key, User user)
	{
		table.put(key, user);
	}

	public User get(String key)
	{
		return table.get(key);
	}

	public Set<String> keySet()
	{
		return table.keySet();
	}
}