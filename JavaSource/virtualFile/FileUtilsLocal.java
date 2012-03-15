package virtualFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class FileUtilsLocal extends FileUtils {
	
	
	public FileUtilsLocal(String location) throws IOException, URISyntaxException {
	
		super(location);
				
	}
		
	@Override
	public boolean createDirectory(final String name) {
		
		File dir = new File(location.getDiskPathWithouthFilename() + name);
		
		boolean success = dir.mkdir();
				
		return(success);
	}
	
	@Override
	public boolean exists(String name) {
		
		File file = new File(location.getDiskPathWithouthFilename() + name);
		
		return(file.exists());
	}
	
	@Override
	public boolean deleteFile(String name) {
				
		File path = new File(location.getDiskPathWithouthFilename() + name);
		
		boolean success = path.delete();
					
		return(success);
		
	}
	
	@Override
	public boolean renameFile(final String oldName, final String newName)  {
	
		String oldLocation = location.getDiskPathWithouthFilename() + oldName;
		String newLocation = location.getDiskPathWithouthFilename() + newName;
		
		File oldItem = new File(oldLocation);
		File newItem = new File(newLocation);
		
		boolean success = oldItem.renameTo(newItem);
							
		return(success);
	
	}
		
	@Override
	public void getDirectoryContents(ArrayList<FileInfo> contents) throws IOException
	{
				
		File dir = new File(location.getDiskPathWithouthFilename());
		
		File[] item = dir.listFiles();
		
		if(item == null) return;
		
		for(int i = 0; i < item.length; i++) {
							
			String name = item[i].getName();			
			long sizeBytes = item[i].length();			
			boolean isDirectory = item[i].isDirectory();
			
			Location fileLocation = null;
			
			try {
				location.setFilename(name);
				fileLocation = (Location)location.clone();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FileInfo info = 
					new FileInfo(name, fileLocation, sizeBytes, isDirectory);
			
			contents.add(info);			
		}
				
	}
	
	@Override
	public boolean deleteDirectory(String name) {
		
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


		
}
