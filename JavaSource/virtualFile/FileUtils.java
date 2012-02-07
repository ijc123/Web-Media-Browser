package virtualFile;

import java.io.IOException;
import java.util.ArrayList;

import database.MediaItem;

public abstract class FileUtils implements LocationInterface {

	protected Location location;
	
	abstract public boolean createDirectory(final String name) throws IOException;
	abstract public boolean exists(final String name) throws IOException;
	abstract public boolean deleteFile(final String name) throws IOException;
	abstract public boolean deleteDirectory(final String name) throws IOException;
	abstract public boolean renameFile(final String oldName, final String newName) throws IOException;	
	
	abstract public void getDirectoryContents(ArrayList<FileInfo> directory, 
			ArrayList<FileInfo> file) throws IOException; 
		
		
	protected FileUtils(String uri) {
		
		location = LocationFactory.create(uri);
	}
			
	// move a directory up in the tree
	public boolean moveUp() {
		
		String curPath = location.getPath();
		
		int pos = curPath.substring(0, curPath.length() - 1).lastIndexOf("/");
		
		if(pos == -1) return(false);
		
		StringBuffer s = new StringBuffer(curPath);
		
		curPath = s.delete(pos + 1, curPath.length()).toString();
		
		location.setPath(curPath);
		
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
		
		String curPath = location.getPath();
		
		curPath = curPath + downDir;
	
		location.setPath(curPath);
	}
	
	public void getDirectoryContents(ArrayList<FileInfo> directory, 
			ArrayList<FileInfo> file, String wildCard) throws IOException
	{
		
		getDirectoryContents(directory, file);
		
		int i = 0;
		
		while(directory != null && i < directory.size()) {
		
			if(wildCardMatch(directory.get(i).getName(), wildCard) == false) {
			
				directory.remove(i);
				i--;
			}
		
			i++;
		}
		
		i = 0;
		
		while(file != null && i < file.size()) {
		
			if(wildCardMatch(file.get(i).getName(), wildCard) == false) {
			
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
				
		ArrayList<FileInfo> directory = new ArrayList<FileInfo>();
		ArrayList<FileInfo> file = new ArrayList<FileInfo>();
				
		getDirectoryContents(directory, file);
		
		for(int i = 0; i < directory.size(); i++) {
			
			moveDown(directory.get(i).getName());
						
			getRecursiveMediaItems(media, video, audio, images, typeName);
			
			moveUp();
		}
	
		for(int i = 0; i < file.size(); i++) {
			
			FileInfo curFile = file.get(i);
			
			MediaItem diskMedia = new MediaItem();
			
			diskMedia.setUri(curFile.getUri());
			diskMedia.setFileName(curFile.getName());
			diskMedia.setSizeBytes(curFile.getSizeBytes());
			
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
		
	@Override
	public void setPath(String path) {
		
		location.setPath(path);
	}
	
	@Override
	public String getPath() {
	
		return(location.getPath());
	}
	
	@Override
	public String getPathWithFilename() {
		
		return(location.getPathWithFilename());
	}
	
	@Override
	public void setFilename(String filename) {
		
		location.setFilename(filename);
	}
	
	@Override
	public void setFilenameWithoutExtension(String filename) {
		
		location.setFilenameWithoutExtension(filename);
	}
	
	@Override
	public String getFilenameWithoutExtension() {
		
		return(location.getFilenameWithoutExtension());
	}
	
	@Override
	public String getFilename() {
		
		return(location.getFilename());
	}
	
	@Override
	public void setExtension(String extension) {
		
		location.setExtension(extension);
	}
	
	@Override
	public String getExtension() {
		
		return(location.getExtension());
	}
	
	@Override
	public boolean isWithoutFilename() {
		
		return(location.isWithoutFilename());
	}
	
	@Override
	public String getLocation() {
		
		return(location.getLocation());
	}

	@Override
	public String getLocationWithoutFilename() {
	
		return(location.getLocationWithoutFilename());	
	}
}

