package virtualFile;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;

public class FileUtilsRemote extends FileUtils {
	
	MyFTPClient ftp;
	
	public FileUtilsRemote(String location) throws IOException, URISyntaxException {
		
		super(location);
		
		URL url = new URL(this.location.getEncodedURL());
		
		ftp = new MyFTPClient(url);
		
	}
	
	@Override
	public boolean createDirectory(final String name) throws IOException {
		
		connect();
		
		boolean success = ftp.makeDirectory(name);
		
		disconnect();
				
		return(success);
	}
	
	@Override
	public boolean exists(String name) throws IOException {
		
		connect();
		
		FTPFile[] file = ftp.listFiles(name);
		
		disconnect();
		
		return(file.length == 0 ? true : false);
	}
	
	@Override
	public boolean deleteFile(String name) throws IOException {
				
		connect();
		
		boolean success = ftp.deleteFile(name);
		
		disconnect();
				
		return(success);
		
	}
	
	@Override
	public boolean renameFile(final String oldName, final String newName) throws IOException {
	
		connect();
		
		boolean success = ftp.rename(oldName, newName);
				
		disconnect();
		
		return(success);
	
	}
	
	@Override
	public void getDirectoryContents(ArrayList<FileInfo> contents) throws IOException 
	{
			
		connect();
		
		FTPFile[] fileList = ftp.listFiles();
				
		for(int i = 0; i < fileList.length; i++) {
						
			String name = fileList[i].getName();			
			long sizeBytes = fileList[i].getSize();		
			
			Location fileLocation = null;
			
			try {
				location.setFilename(name);
				fileLocation = (Location)location.clone();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean isDirectory = fileList[i].isDirectory();
			
			FileInfo info = new FileInfo(name, fileLocation, sizeBytes, isDirectory);
			
			contents.add(info);
						
		}
			
		disconnect();
	}

	@Override
	public boolean deleteDirectory(String name) throws IOException {
		
		connect();
		
		boolean success = ftp.removeDirectory(name);
		
		disconnect();
				
		return(success);
		
	}

	private void connect() throws SocketException, IOException {
		
		ftp.connect();
		ftp.cwd(location.getPath());
	}
	
	private void disconnect() {
		
		ftp.disconnect();
	}
	

}
