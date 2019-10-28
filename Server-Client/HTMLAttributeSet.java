public class HTMLAttributeSet
{
    private String attr;
    private String value;

    public HTMLAttributeSet()
    {
        attr  = "";
        value = "";
    }

    public HTMLAttributeSet(String attr, String value)
    {
        this.attr  = attr;
        this.value = value;
    }

    public void setAttribute(String attr)
    {
        this.attr = attr;
    }

    public String getAttribute()
    {
        return attr;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}