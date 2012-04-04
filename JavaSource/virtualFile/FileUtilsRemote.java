package virtualFile;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;

import debug.Log;

public class FileUtilsRemote extends FileUtils {
	
	MyFTPClient ftp;
	
	public FileUtilsRemote(String location) throws IOException, URISyntaxException {
		
		super(location);
		
		URL url = new URL(this.location.getEncodedURL());
		
		ftp = new MyFTPClient(url);
		
		ftp.connect();
		
		ftp.setListHiddenFiles(true);
	}
	
	@Override
	public boolean createDirectory(final String name) throws IOException {
		
		setPath();
		
		boolean success = ftp.makeDirectory(name);
						
		return(success);
	}
	
	@Override
	public boolean exists(String name) throws IOException {
		
		setPath();
		
		FTPFile[] file = ftp.listFiles(name);
		
		return(file.length == 0 ? true : false);
	}
	
	@Override
	public boolean deleteFile(String name) throws IOException {
				
		setPath();
		
		boolean success = ftp.deleteFile(name);
				
		return(success);
		
	}
	
	@Override
	public boolean renameFile(final String oldName, final String newName) throws IOException {
	
		setPath();
		
		boolean success = ftp.rename(oldName, newName);			
		
		return(success);
	
	}
	
	@Override
	public void getDirectoryContents(ArrayList<FileInfo> contents) throws IOException 
	{

		// if we cannot set the current path (no permission?) return a 
		// empty directory
		if(setPath() == false) return;
		
		// when multiple ftp commands are run too fast in succession 
		// some FTP servers will kill the connection.
		// so in that case we sleep a little while in between
		try {
			Thread.sleep(getTimeout());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		FTPFile[] fileList = ftp.listFiles();
				
		for(int i = 0; i < fileList.length; i++) {
						
			String name = fileList[i].getName();	
			
			// skip useless directories
			if(name.equals(".")) continue;
			if(name.equals("..")) continue;		
									
			Log.info(this, name);
			
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
		
	}

	@Override
	public boolean deleteDirectory(String name) throws IOException {
		
		setPath();
		
		boolean success = ftp.removeDirectory(name);
				
		return(success);
		
	}

	private boolean setPath() throws SocketException, IOException {
		
		String path = location.getPath();
		
		int status = ftp.cwd(path);
		
		Log.info(this, path);
		
		if(status != 250) {
		
			// can be incorrect path or incorrect permissions etc
			return(false);
		
		}
		
		return(true);
	}
	
	@Override
	public void close() {
		
		ftp.disconnect();
		
	}
	

}
