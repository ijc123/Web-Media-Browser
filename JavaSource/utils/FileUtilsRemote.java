package utils;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;

import ftp.MyFTPClient;

public class FileUtilsRemote extends FileUtils {
	
	MyFTPClient ftp;
	
	public FileUtilsRemote(String url) {
		
		super();
		
		ftp = new MyFTPClient(url);
		
		setPath(ftp.getPath());
				
	}
			
	public boolean createDirectory(final String name) throws IOException {
		
		ftp.connect();
		ftp.cwd(curPath);
		
		boolean success = ftp.makeDirectory(name);
		
		ftp.disconnect();
				
		return(success);
	}
	
	public boolean exists(final String fileName) throws IOException {
		
		ftp.connect();
		ftp.cwd(curPath);
		
		FTPFile[] file = ftp.listFiles(fileName);
		
		ftp.disconnect();
		
		return(file.length == 0 ? true : false);
	}
	
	public boolean delete(final String filePath) throws IOException {
				
		ftp.connect();
		ftp.cwd(curPath);
		
		boolean success = ftp.deleteFile(filePath);
		
		ftp.disconnect();
				
		return(success);
		
	}
	
	public boolean rename(final String oldName, final String newName) throws IOException {
	
		ftp.connect();
		ftp.cwd(curPath);
		
		boolean success = ftp.rename(oldName, newName);
				
		ftp.disconnect();
		
		return(success);
	
	}
	
	public void getDirectoryContents(ArrayList<String> directory, 
			ArrayList<String> file) throws IOException 
	{
			
		ftp.connect();
		ftp.cwd(curPath);
		
		FTPFile[] fileList = ftp.listFiles();
			
		for(int i = 0; i < fileList.length; i++) {
			
			if(fileList[i].isDirectory()) {
				
				if(directory != null) {
				
					directory.add(fileList[i].getName());
				}
				
			} else {
				
				if(file != null) {
					
					file.add(fileList[i].getName());
				}
			}
			
		}
			
		ftp.disconnect();
	}

}
