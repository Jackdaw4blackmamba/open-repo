import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.*;
import java.security.cert.*;

public class Talker
{
	private BufferedReader   br;
	private DataOutputStream dos;

	private String           id;

	private static final int PORT_NUM = 6789;

	public Talker(String serverDomainName, int portNumber, String id) throws Exception
	{
		SSLSocketFactory sslSocketFactory;
		SSLContext       sslContext;
		KeyManagerFactory keyManagerFactory;
		KeyStore          keyStore;
		char[]            keyStorePassphrase;

		SSLSocket         sslSocket;

		System.setProperty("javax.net.ssl.trustStore", "samplecacerts");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

		sslContext = SSLContext.getInstance("SSL");
		keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyStore = KeyStore.getInstance("JKS");

		keyStorePassphrase = "passphrase".toCharArray();
		keyStore.load(new FileInputStream("testkeys"), keyStorePassphrase);

		keyManagerFactory.init(keyStore, keyStorePassphrase);
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

		sslSocketFactory = sslContext.getSocketFactory();

		sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

		sslSocket = (SSLSocket)sslSocketFactory.createSocket(serverDomainName, portNumber);
		sslSocket.startHandshake();

		br  = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
		dos = new DataOutputStream(sslSocket.getOutputStream());

		this.id = id;
	}

	public Talker(SSLSocket sslSocket) throws IOException
	{
		br  = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
		dos = new DataOutputStream(sslSocket.getOutputStream());
	}

	public void send(String msg) throws IOException
	{
		if(!msg.endsWith("\n"))
		    msg += "\n";
		dos.writeBytes(msg);
		if(id != null)
		    System.out.println("Sent[" + id + "]: " + msg.substring(0, msg.length() - 1));
		else
		    System.out.println("Sent: " + msg.substring(0, msg.length() - 1));
	}

	public String receive() throws IOException
	{
		String msg;
		msg = br.readLine();
		if(id != null)
		    System.out.println("Received[" + id + "]: " + msg);
		else
		    System.out.println("Received: " + msg);
		return msg;
	}

	public String getID()
	{
		return id;
	}
}