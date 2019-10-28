import java.util.*;

public class HTMLComponent
{
    private HTMLTag tag;
    private List<HTMLAttributeSet> attributeSets;
    private String text;
    private List<HTMLComponent> children;

    public HTMLComponent()
    {
        tag           = HTMLTag.UNDEFINED;
        attributeSets = new ArrayList<HTMLAttributeSet>();
        text = null;
        children = new ArrayList<HTMLComponent>();
    }

    public HTMLComponent(HTMLTag tag)
    {
        this.tag      = tag;
        attributeSets = new ArrayList<HTMLAttributeSet>();
        text = null;
        children = new ArrayList<HTMLComponent>();
    }

    public void setTag(HTMLTag tag)
    {
        this.tag = tag;
    }

    public HTMLTag getTag()
    {
        return tag;
    }

    public void addAttributeSet(HTMLAttributeSet attr)
    {
        attributeSets.add(attr);
    }

    public HTMLAttributeSet getAttributeSet(int index)
    {
        return attributeSets.get(index);
    }

    public int getAttributeSetCount()
    {
        return attributeSets.size();
    }

    public void clearAttributeSets()
    {
        attributeSets.clear();
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public void setValue(String attrKey, String attrVal)
    {
        for(int i = 0; i < getAttributeSetCount(); i++)
            if(attrKey.toLowerCase().equals(getAttributeSet(i).getAttribute().toLowerCase()))
                getAttributeSet(i).setValue(attrVal);
    }

    public String getValue(String attr)
    {
        for(int i = 0; i < getAttributeSetCount(); i++)
            if(attr.toLowerCase().equals(getAttributeSet(i).getAttribute().toLowerCase()))
                return getAttributeSet(i).getValue();
        return null;
    }

    public boolean isSimpleTagged()
    {
        return HTMLUtilities.isSimpleTag(tag);
    }

    public void addChild(HTMLComponent child)
    {
		children.add(child);
	}

	public HTMLComponent getChild(int index)
	{
		return children.get(index);
	}

	public int getChildrenCount()
	{
		return children.size();
	}

	public boolean insertIntoFirstTag(HTMLComponent compToInsert, HTMLTag tag)
	{
		for(int i = 0; i < children.size(); i++)
		{
			if(children.get(i).getTag() == tag)
			{
				children.get(i).addChild(compToInsert);
				return true;
			}
			else if(children.get(i).isParent())
			    if(insertIntoFirstTag(children.get(i), tag))
			        return true;
		}
		return false;
	}

	public boolean insertIntoLastTag(HTMLComponent compToInsert, HTMLTag tag)
	{
		for(int i = children.size() - 1; i >= 0; i--)
		{
			if(children.get(i).getTag() == tag)
			{
				children.get(i).addChild(compToInsert);
				return true;
			}
			else if(children.get(i).isParent())
				if(insertIntoLastTag(children.get(i), tag))
				    return true;
		}
		return false;
	}

	public boolean isParent()
	{
		return children.size() > 0;
	}
}