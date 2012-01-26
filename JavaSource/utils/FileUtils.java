package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import database.MediaItem;



public abstract class FileUtils {

	protected String curPath;
	
	abstract public boolean createDirectory(final String name) throws IOException;
	abstract public boolean exists(final String fileName) throws IOException;
	abstract public boolean delete(final String filePath) throws IOException;	
	abstract public boolean rename(final String oldName, final String newName) throws IOException;
	
	abstract public void getDirectoryContents(ArrayList<String> directory, 
			ArrayList<String> file) throws IOException; 
		
	protected FileUtils() {
		
		curPath = "";
	}
		
	protected FileUtils(String path) {
		
		curPath = path;
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
	
	public void getDirectoryContents(ArrayList<String> directory, 
			ArrayList<String> file, String wildCard) throws IOException
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

	public void getRecursiveMediaItems(ArrayList<MediaItem> media, 
			boolean video, boolean audio, boolean images, String typeName)
		throws IOException
	{
				
		ArrayList<String> directory = new ArrayList<String>();
		ArrayList<String> filename = new ArrayList<String>();
				
		getDirectoryContents(directory, filename);
		
		for(int i = 0; i < directory.size(); i++) {
			
			moveDown(directory.get(i));
						
			getRecursiveMediaItems(media, video, audio, images, typeName);
			
			moveUp();
		}
	
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
	
	public static ArrayList<String> getRootPaths() {
		
		ArrayList<String> rootPath = new ArrayList<String>();
		
		File[] roots = File.listRoots();
		
		for(int i = 0; i < roots.length; i++) {
			
			rootPath.add(roots[i].getPath().replace('\\', '/'));
			
		}
	
		return(rootPath);
	}
		 
	/**
	 * @param path
	 * @return
	 * 0 = drive
	 * 1 = directory
	 * 2 = filename
	 * 3 = extension
	 */
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

