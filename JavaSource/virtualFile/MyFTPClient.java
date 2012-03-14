package virtualFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTPClient extends FTPClient {

	String host;
	String username;
	String password;
	int port;
	 
	public MyFTPClient(URL url) {

		super();

		Location location;
		
		try {
			location = new Location(url.toString());
			host = location.getHost();
			username = location.getUsername();
			password = location.getPassword();
			port = url.getPort();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
		
	public void connect() throws SocketException, IOException {

		if(isConnected()) return;
		
		try {
			
			if(port == -1) {
			
				super.connect(host);
				
			} else {
				
				super.connect(host, port);
			}
			
			login(username, password);
		
			// After connection attempt, you should check the reply code to verify
			// success.
			int reply = getReplyCode();

			if(!FTPReply.isPositiveCompletion(reply)) {
				disconnect();			
				throw new IOException("FTP server refused connection: " + host);			
			}
			
			
		} catch(IOException e) {

			e.printStackTrace();
			
		}

	}
		
	@Override
	public void disconnect() {

		if(isConnected()) {
			
			try {
				
				super.disconnect();
				
			} catch(IOException ioe) {
				// do nothing
			}
		}

	}
	
}
