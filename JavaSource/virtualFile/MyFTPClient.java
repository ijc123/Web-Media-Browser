package virtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTPClient extends FTPClient {

	private LocationRemote location;
		
	public MyFTPClient(String url) {

		super();

		location = new LocationRemote(url);
			
	}
	
	public MyFTPClient(String url, String username, String password) {

		super();

		location = new LocationRemote(url, username, password);
			
	}
	
	public void connect() throws SocketException, IOException {

		if(isConnected()) return;
		
		try {
			
			if(location.getPort() == -1) {
			
				super.connect(location.getHost());
				
			} else {
				
				super.connect(location.getHost(), location.getPort());
			}
			
			login(location.getUsername(), location.getPassword());
		
			// After connection attempt, you should check the reply code to verify
			// success.
			int reply = getReplyCode();

			if(!FTPReply.isPositiveCompletion(reply)) {
				disconnect();			
				throw new IOException("FTP server refused connection: " + location.getLocation());			
			}
			
			
		} catch(IOException e) {

			e.printStackTrace();
			
		}

	}
	
	public FTPFile getFileInfo() throws IOException {
		
		if(location.isWithoutFilename()) return(null);
		
		cwd(location.getPath());
		
		String filename = location.getFilename();
		
		FTPFile[] file = listFiles(filename);	
		
		if(file.length > 0) {
			
			return(file[0]);
		}
		
		return(null);
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
	

	public InputStream retrieveFileStream() throws IOException {
		
		cwd(location.getPath());
		
		return(retrieveFileStream(location.getFilename()));
	}

	LocationRemote getLocation() {
		
		return(location);
	}

}
