import java.io.*;
import java.net.*;

public class BabyServer implements Runnable
{
	private Socket socket;
	private String filename;
	private long   totalFileSize;

	private static final int BUFFER_SIZE = 100;

	public BabyServer(Socket socket, String filename, long totalFileSize) throws IOException
	{
		this.socket        = socket;
		this.filename      = filename;
		this.totalFileSize = totalFileSize;

		new Thread(this).start();
	}

	public void run()
	{
		InputStream      inputStreamFromNet;
	    FileOutputStream outputStreamToFile;
		byte[]           buffer;
		int              numBytesRead;
		long             totalNumBytesRead;

		try
		{
		    inputStreamFromNet = socket.getInputStream();
		    outputStreamToFile = new FileOutputStream(new File(filename));
		    buffer             = new byte[BUFFER_SIZE];
		    numBytesRead       = inputStreamFromNet.read(buffer);
		    totalNumBytesRead  = 0;
            while(totalNumBytesRead < totalFileSize)
            {
		    	totalNumBytesRead += numBytesRead;
		    	outputStreamToFile.write(buffer, 0, numBytesRead);
		    	numBytesRead = inputStreamFromNet.read(buffer);
		    }
		    inputStreamFromNet.close();
		    outputStreamToFile.close();
		    socket.close();
		    MessageDialogUtilities.showMessage("\"" + filename + "\" downloaded successfully.", "Information");
		}
		catch(IOException ioe)
		{
			MessageDialogUtilities.showErrorMessage("Error: IO exception...");
		}
	}
}