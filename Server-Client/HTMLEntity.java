import java.util.*;

public class HTMLEntity
{
	private List<HTMLComponent> comps;

	public HTMLEntity()
	{
		comps = new ArrayList<HTMLComponent>();
	}

	public void addHTMLComponent(HTMLComponent comp)
	{
		comps.add(comp);
	}

	public HTMLComponent getHTMLComponent(int index)
	{
		return comps.get(index);
	}

	public int getHTMLComponentCount()
	{
		return comps.size();
	}

	public void removeHTMLComponent(int index)
	{
		comps.remove(index);
	}

	public void clearHTMLComponents()
	{
		comps.clear();
	}

	public boolean insertIntoFirstTag(HTMLComponent compToInsert, HTMLTag tag)
	{
		for(int i = 0; i < comps.size(); i++)
			if(comps.get(i).insertIntoFirstTag(compToInsert, tag))
			    return true;
		return false;
	}

	public boolean insertIntoLastTag(HTMLComponent compToInsert, HTMLTag tag)
    {
		for(int i = comps.size() - 1; i >= 0; i--)
			if(comps.get(i).insertIntoLastTag(compToInsert, tag))
			    return true;
		return false;
}

	private String space(int level)
	{
		String tmp;
		tmp = "";
		for(int i = 0; i < level; i++)
		    tmp += "  ";
		return tmp;
	}

	public String toString()
	{
		String str;
		str = "";
		for(int i = 0; i < comps.size(); i++)
		    str += crawl(comps.get(i), 0);
		return str;
	}

	private String crawl(HTMLComponent comp, int level)
	{
		String str;
		str = space(level) + "<" + HTMLUtilities.getTagText(comp.getTag());
		for(int i = 0; i < comp.getAttributeSetCount(); i++)
		    str += " " + comp.getAttributeSet(i).getAttribute() + "=" + comp.getAttributeSet(i).getValue();
		str += ">";
		if(comp.getText() != null)
		    str += space(level + 1) + comp.getText();

		for(int i = 0; i < comp.getChildrenCount(); i++)
			str += crawl(comp.getChild(i), level + 1);

		if(!HTMLUtilities.isSimpleTag(comp.getTag()))
		    str += space(level) + "</" + HTMLUtilities.getTagText(comp.getTag()) + ">";

		return str;
	}
}