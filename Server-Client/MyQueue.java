import java.util.*;

public class MyQueue<T>
{
	private List<T> list;

	public MyQueue()
	{
		list = new ArrayList<T>();
	}

	public void add(T t)
	{
		list.add(t);
	}

	public T peek()
	{
		if(list.size() == 0)
		    return null;
		return list.get(0);
	}

	public T poll()
	{
		if(list.size() == 0)
		    return null;
		return list.remove(0);
	}

	public boolean hasNext()
	{
		return list.size() != 0;
	}

	public int size()
	{
		return list.size();
	}

	public MyQueue<T> clone()
	{
		MyQueue<T> queue;
		queue = new MyQueue<T>();
		for(int i = 0; i < size(); i++)
		    queue.add(list.get(i));
		return queue;
	}
}