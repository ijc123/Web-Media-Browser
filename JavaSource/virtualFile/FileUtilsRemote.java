package virtualFile;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;



public class FileUtilsRemote extends FileUtils {
	
	MyFTPClient ftp;
	
	public FileUtilsRemote(String url) {
		
		super(url);
		
		ftp = new MyFTPClient(url);
		
	}
			
	@Override
	public boolean createDirectory(final String name) throws IOException {
		
		ftp.connect();
		ftp.cwd(location.getPath());
		
		boolean success = ftp.makeDirectory(name);
		
		ftp.disconnect();
				
		return(success);
	}
	
	@Override
	public boolean exists(String name) throws IOException {
		
		ftp.connect();
		ftp.cwd(location.getPath());
		
		FTPFile[] file = ftp.listFiles(name);
		
		ftp.disconnect();
		
		return(file.length == 0 ? true : false);
	}
	
	@Override
	public boolean deleteFile(String name) throws IOException {
				
		ftp.connect();
		ftp.cwd(location.getPath());
		
		boolean success = ftp.deleteFile(name);
		
		ftp.disconnect();
				
		return(success);
		
	}
	
	@Override
	public boolean renameFile(final String oldName, final String newName) throws IOException {
	
		ftp.connect();
		
		ftp.cwd(location.getPath());
		
		boolean success = ftp.rename(oldName, newName);
				
		ftp.disconnect();
		
		return(success);
	
	}
	
	@Override
	public void getDirectoryContents(ArrayList<FileInfo> directory, 
			ArrayList<FileInfo> file) throws IOException 
	{
			
		ftp.connect();
		ftp.cwd(location.getPath());
		
		FTPFile[] fileList = ftp.listFiles();
				
		for(int i = 0; i < fileList.length; i++) {
			
			String uri = ftp.getLocation().getLocationWithoutUserInfo() + fileList[i].getName(); 
			
			if(fileList[i].isDirectory()) {
				
				if(directory != null) {
				
					directory.add(new FileInfo(fileList[i], uri));
				}
				
			} else {
				
				if(file != null) {
					
					file.add(new FileInfo(fileList[i], uri));
				}
			}
			
		}
			
		ftp.disconnect();
	}

	@Override
	public boolean deleteDirectory(String name) throws IOException {
		
		ftp.connect();
		
		boolean success = ftp.removeDirectory(location.getPath() + name);
		
		ftp.disconnect();
				
		return(success);
		
	}

	

}
