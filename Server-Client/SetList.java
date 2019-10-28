import java.util.*;

public class SetList<T>
{
	private List<T> list;

	public SetList()
	{
		list = new ArrayList<T>();
	}

	public void add(T t)
	{
		boolean found;
		found = false;
		for(int i = 0; i < list.size(); i++)
		    if(list.get(i).equals(t))
		    {
				found = true;
				break;
			}
		if(!found)
		    list.add(t);
	}

	public T get(int index)
	{
		return list.get(index);
	}

	public void remove(int index)
	{
		list.remove(index);
	}

	public int size()
	{
		return list.size();
	}
}