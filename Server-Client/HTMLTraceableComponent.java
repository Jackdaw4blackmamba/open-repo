public class HTMLTraceableComponent extends HTMLComponent
{
	private HTMLTraceableComponent parent;

	public HTMLTraceableComponent(HTMLTraceableComponent parent)
	{
		super();
		this.parent = parent;
	}

	public void setParent(HTMLTraceableComponent parent)
	{
		this.parent = parent;
	}

	public HTMLTraceableComponent getParent()
	{
		return parent;
	}
}