package ftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import utils.FileUtils;

public class MyFTPClient extends FTPClient {

	private String username;
	private String password;
	private String host;
	private String path;
	private String fileName;
	
	private int port;
		
	public MyFTPClient(String url) {

		super();

		URI uri;
		
		try {
			
			uri = new URI(url);
			
			host = uri.getHost();
			
			String[] splitPath = FileUtils.splitPath(uri.getPath());
			
			path = splitPath[1];
			
			if(!splitPath[3].isEmpty()) splitPath[3] = "." + splitPath[3];			
			fileName = splitPath[2] + splitPath[3];
			
			port = uri.getPort();		
			
			String userInfo = uri.getUserInfo();
			
			if(userInfo != null) {
				
				int seperator = userInfo.indexOf(':');
				
				username = userInfo.substring(0, seperator);
				password = userInfo.substring(seperator + 1);
			}
			
		} catch (URISyntaxException e) {
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
				throw new IOException("FTP server refused connection: " + path);			
			}
			
			
		} catch(IOException e) {

			e.printStackTrace();
			
		}

	}
	
	public FTPFile getFileInfo() throws IOException {
		
		cwd(path);
		FTPFile[] file = listFiles(fileName);
		
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
		
		cwd(path);
		
		return(retrieveFileStream(getFileName()));
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	public String getFileName() {		
		return fileName;
	}
}
