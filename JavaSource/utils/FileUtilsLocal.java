package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtilsLocal extends FileUtils {
	
	public FileUtilsLocal(String path) {
	
		super(path);
		
	}
		
	public boolean createDirectory(final String name) throws IOException {
		
		File dir = new File(curPath + name);
		
		boolean success = dir.mkdir();
		
		if(success == false) {
			
			String exception = String.format("Unable to create directory '%s'.", curPath + name);
			
			throw new IOException(exception);
		}
				
		return(success);
	}
	
	public boolean exists(final String fileName) throws IOException {
		
		File file = new File(curPath + fileName);
		
		return(file.exists());
	}
	
	public boolean delete(final String filePath) throws IOException {
		
		
		File path = new File(curPath + filePath);
		
		boolean success = path.delete();
		
		if(success == false) {
			
			String exception = String.format("Unable to delete  '%s'.", curPath + filePath);
			
			throw new IOException(exception);
		}
				
		return(success);
		
	}
	
	public boolean rename(final String oldName, final String newName) throws IOException {
	
		File oldItem = new File(curPath + oldName);
		File newItem = new File(curPath + newName);
		
		boolean success = oldItem.renameTo(newItem);
		
		if(success == false) {
			
			String exception = String.format("Unable to rename '%s' to '%s'.", curPath + oldName, curPath + newName);
			
			throw new IOException(exception);
		}
				
		return(success);
	
	}
		
	public void getDirectoryContents(ArrayList<String> directory, 
			ArrayList<String> file) throws IOException
	{
				
		File location = new File(curPath);
		
		File[] item = location.listFiles();
		
		if(item == null) return;
		
		for(int i = 0; i < item.length; i++) {
			
			if(item[i].isDirectory()) {
				
				if(directory != null) {
				
					directory.add(item[i].getName());
				}
				
			} else {
				
				if(file != null) {
					
					file.add(item[i].getName());
				}
			}
			
		}
				
	}
		

}
