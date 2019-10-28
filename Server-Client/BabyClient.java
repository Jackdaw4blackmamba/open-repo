import java.io.*;
import java.net.*;

public class BabyClient implements Runnable
{
	private Socket socket;
	private String filename;
	private long   totalFileSize;

	private static final int BUFFER_SIZE = 100;

	public BabyClient(Socket socket, String filename, long totalFileSize) throws IOException
	{
		this.socket        = socket;
		this.filename      = filename;
		this.totalFileSize = totalFileSize;

		new Thread(this).start();
	}

	public void run()
	{
		FileInputStream inputStreamFromFile;
		OutputStream    outputStreamToNet;
		byte[]          buffer;
		int             numBytesRead;
		long            totalNumBytesRead;

		try
		{
			inputStreamFromFile = new FileInputStream(new File(filename));
			outputStreamToNet   = socket.getOutputStream();
			buffer              = new byte[BUFFER_SIZE];
			numBytesRead        = inputStreamFromFile.read(buffer);
			totalNumBytesRead   = 0;
			while(totalNumBytesRead < totalFileSize)
			{
				totalNumBytesRead += numBytesRead;
				outputStreamToNet.write(buffer, 0, numBytesRead);
				numBytesRead = inputStreamFromFile.read(buffer);
			}
			inputStreamFromFile.close();
			outputStreamToNet.close();
			socket.close();
		}
		catch(IOException ioe)
		{
			MessageDialogUtilities.showErrorMessage("Error: IO exception...");
		}
	}
}