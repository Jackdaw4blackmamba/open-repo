import java.io.*;

public abstract class Visitor
{
	protected String[] parts;
	protected Runnable ioException;

	public Visitor(String[] parts, Runnable ioException)
	{
		this.parts = parts;
		this.ioException = ioException;
	}

	public abstract void visit(CTC ctc);
	public abstract void visit(CTS cts);
}