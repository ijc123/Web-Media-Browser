package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import database.MediaItem;

public class FileUtils {

	private String curPath;
	
	public FileUtils() {
		
		curPath = "";
	}
	
	public FileUtils(String path) {
		
		setPath(path);
	}
	
	public void setPath(final String path) {
				
		curPath = path;
		curPath = curPath.replace('\\', '/');
		
		if(!curPath.endsWith("/")) {
			
			curPath = curPath + "/";
		}
	}
	
	public String getPath() {
		
		return(curPath);
	}
	
	// move a directory up in the tree
	public boolean moveUp() {
		
		int pos = curPath.substring(0, curPath.length() - 1).lastIndexOf("/");
		
		if(pos == -1) return(false);
		
		StringBuffer s = new StringBuffer(curPath);
		
		curPath = s.delete(pos + 1, curPath.length()).toString();
		
		return(true);
	}
	
	// move a directory down in the tree
	public void moveDown(final String directory) {
		
		if(directory == null) return;
		
		String downDir = directory.replace('\\','/');
		
		if(!downDir.endsWith("/")) {
			
			downDir = downDir  + "/";			
		}
		
		if(downDir.startsWith("/")) {
			
			downDir = downDir.substring(1);
		}
				
		curPath = curPath + downDir;
	
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
	
	public boolean exists(final String fileName) {
		
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
	
	public ArrayList<String> getRootPaths() {
		
		ArrayList<String> rootPath = new ArrayList<String>();
		
		File[] roots = File.listRoots();
		
		for(int i = 0; i < roots.length; i++) {
			
			rootPath.add(roots[i].getPath().replace('\\', '/'));
			
		}
	
		return(rootPath);
	}
	
	public void getDirectoryContents(ArrayList<String> directory, 
			ArrayList<String> file) 
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
	
	public void getDirectoryContents(ArrayList<String> directory, 
			ArrayList<String> file, String wildCard) 
	{
		
		getDirectoryContents(directory, file);
		
		int i = 0;
		
		while(directory != null && i < directory.size()) {
		
			if(wildCardMatch(directory.get(i), wildCard) == false) {
			
				directory.remove(i);
				i--;
			}
		
			i++;
		}
		
		i = 0;
		
		while(file != null && i < file.size()) {
		
			if(wildCardMatch(file.get(i), wildCard) == false) {
			
				file.remove(i);
				i--;
			}
		
			i++;
		}
			
	}
	
	private boolean wildCardMatch(String text, String pattern)
    {
        // Create the cards by splitting using a RegEx. If more speed 
        // is desired, a simpler character based splitting can be done.
        String [] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards)
        {
            int idx = text.indexOf(card);
            
            // Card not detected in the text.
            if(idx == -1)
            {
                return false;
            }
            
            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }
        
        return true;
    }
/*	
	public enum FileInfoMode {
		DIRECTORIES_ONLY,
		FILES_ONLY,
		FILES_AND_DIRECTORIES
	}
*/	
	public void getRecursiveMediaItems(ArrayList<MediaItem> media, boolean video, boolean audio, boolean images, String typeName) {
				
		ArrayList<String> directory = new ArrayList<String>();
		ArrayList<String> filename = new ArrayList<String>();
				
		getDirectoryContents(directory, filename);
		
		for(int i = 0; i < directory.size(); i++) {
			
			moveDown(directory.get(i));
						
			getRecursiveMediaItems(media, video, audio, images, typeName);
			
			moveUp();
		}
/*				
		if(mode != FileInfoMode.FILES_ONLY) {
			
			for(int i = 0; i < directory.size(); i++) {
					
				FileInfo info = new FileInfo();
				File curFile = new File(getPath() + directory.get(i));
				
				info.setDirectory(true);
				info.setPath(getPath());
				info.setFileName(directory.get(i));
				info.setUri(curFile.toURI().toString());
				info.setSizeBytes(0);
				
				media.add(info);
			}
			
		}
		
		if(mode != FileInfoMode.DIRECTORIES_ONLY) {
*/		
		for(int i = 0; i < filename.size(); i++) {
			
			File curFile = new File(getPath() + filename.get(i));
			
			MediaItem diskMedia = new MediaItem();

			diskMedia.setUri(curFile.toURI().toString());
			diskMedia.setFileName(filename.get(i));
			diskMedia.setSizeBytes(curFile.length());
			
			ArrayList<String> typeNames = new ArrayList<String>();
			
			typeNames.add(typeName);
			
			diskMedia.setTypeNames(typeNames);
					
			if((video && diskMedia.getMimeType().startsWith("video")) ||
			   (audio && diskMedia.getMimeType().startsWith("audio")) ||
			   (images && diskMedia.getMimeType().startsWith("image")))
			{				
				media.add(diskMedia);
			}
		}
		
		
	}
	
	// 0 = drive
	// 1 = directory
	// 2 = filename
	// 3 = extension
	public static String[] splitPath(String path) {
		
		String[] result = new String[]{null, null, null, null};
		
		int drive = path.indexOf('/');
		int dot = path.lastIndexOf('.');
		int sep = path.lastIndexOf('/');
		
		if(drive != -1) {
			
			result[0] = path.substring(0, drive);
			
			if(sep != -1) {
			
				result[1] = path.substring(drive, sep);
			}
		}
		
		if(dot != -1) {
					
			result[2] = path.substring(sep + 1, dot);						
			result[3] = path.substring(dot + 1);
		}	
		
		return(result);
	
	}
	

}
