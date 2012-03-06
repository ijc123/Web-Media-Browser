package virtualFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FileUtilsLocal extends FileUtils {
	
	
	public FileUtilsLocal(String path) {
	
		super(path);
		
		
	}
	
	@Override
	public boolean createDirectory(final String name) throws IOException {
		
		File dir = new File(location.getLocationWithoutFilename() + name);
		
		boolean success = dir.mkdir();
		
		if(success == false) {
			
			String exception = String.format("Unable to create directory '%s'.", 
					location.getLocation() + name);
			
			throw new IOException(exception);
		}
				
		return(success);
	}
	
	@Override
	public boolean exists(String name) throws IOException {
		
		File file = new File(location.getLocationWithoutFilename() + name);
		
		return(file.exists());
	}
	
	@Override
	public boolean deleteFile(String name) throws IOException {
				
		File path = new File(location.getLocationWithoutFilename() + name);
		
		boolean success = path.delete();
		
		if(success == false) {
			
			String exception = String.format("Unable to delete  '%s'.", 
					location.getLocation());
			
			throw new IOException(exception);
		}
				
		return(success);
		
	}
	
	@Override
	public boolean renameFile(final String oldName, final String newName) throws IOException {
	
		String oldLocation = location.getLocationWithoutFilename() + oldName;
		String newLocation = location.getLocationWithoutFilename() + newName;
		
		File oldItem = new File(oldLocation);
		File newItem = new File(newLocation);
		
		boolean success = oldItem.renameTo(newItem);
		
		if(success == false) {
			
			String exception = String.format("Unable to rename '%s' to '%s'.", 
					oldLocation, newLocation);
			
			throw new IOException(exception);
		}
						
		return(success);
	
	}
		
	@Override
	public void getDirectoryContents(ArrayList<FileInfo> contents) throws IOException
	{
				
		File dir = new File(location.getLocation());
		
		File[] item = dir.listFiles();
		
		if(item == null) return;
		
		for(int i = 0; i < item.length; i++) {
							
			contents.add(new FileInfo(item[i]));			
		}
				
	}
	
	@Override
	public boolean deleteDirectory(String name) throws IOException {
		
		return(deleteFile(name));
	}
	
	public static ArrayList<String> getRootPaths() {
		
		ArrayList<String> rootPath = new ArrayList<String>();
		
		File[] roots = File.listRoots();
		
		for(int i = 0; i < roots.length; i++) {
			
			rootPath.add(roots[i].getPath().replace('\\', '/').toLowerCase());
			
		}
	
		return(rootPath);
	}

	public String getDrive() {
		
		LocationLocal locationLocal = (LocationLocal)location;
		
		return locationLocal.getDrive();
	}

	public void setDrive(String drive) {
		
		LocationLocal locationLocal = (LocationLocal)location;
		
		locationLocal.setDrive(drive);
	}
		
}
