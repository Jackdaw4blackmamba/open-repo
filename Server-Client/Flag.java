public class Flag implements Runnable
{
	private boolean raised;

	public Flag()
	{
		raised = false;
	}

	public Flag(boolean raised)
	{
		this.raised = raised;
	}

	public void up()
	{
		raised = true;
	}

	public void down()
	{
		raised = false;
	}

	public boolean isUp()
	{
		return raised;
	}

	public void run()
	{
	}
}