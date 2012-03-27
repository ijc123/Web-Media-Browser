package virtualFile;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import database.MediaItem;

public abstract class FileUtils implements Closeable {

	protected Location location;
	
	abstract public boolean createDirectory(final String name) throws IOException;
	abstract public boolean exists(final String name) throws IOException;
	abstract public boolean deleteFile(final String name) throws IOException;
	abstract public boolean deleteDirectory(final String name) throws IOException;
	abstract public boolean renameFile(final String oldName, final String newName) throws IOException;	
	abstract public void close();
	
	abstract public void getDirectoryContents(ArrayList<FileInfo> contents) throws IOException; 
		
	private int timeout;
		
	protected FileUtils(String location) throws IOException, URISyntaxException {
		
		this.location = new Location(location);
		timeout = 0;
	}
			
	// move a directory up in the tree
	public boolean moveUp() {
		
		return(location.moveUp());
	}
	
	// move a directory down in the tree
	public void moveDown(final String directory) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		
		location.moveDown(directory);
	}
	
	public void getDirectoryContents(ArrayList<FileInfo> contents, String wildCard) throws IOException
	{
		
		getDirectoryContents(contents);
		
		int i = 0;
		
		while(contents != null && i < contents.size()) {
		
			if(wildCardMatch(contents.get(i).getName(), wildCard) == false) {
			
				contents.remove(i);
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
				
		setTimeout(200);
		
		ArrayList<FileInfo> contents = new ArrayList<FileInfo>();
				
		getDirectoryContents(contents);
		
		for(int i = 0; i < contents.size(); i++) {
			
			if(!contents.get(i).isDirectory()) continue;
						
			try {
				moveDown(contents.get(i).getName());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			getRecursiveMediaItems(media, video, audio, images, typeName);
		
			moveUp();
			
		}
		
		for(int i = 0; i < contents.size(); i++) {
			
			if(contents.get(i).isDirectory()) continue;
			
			FileInfo curFile = contents.get(i);
			
			MediaItem diskMedia = new MediaItem();
			
			diskMedia.setUri(curFile.getLocation().getEncodedURL());
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
		
		setTimeout(0);
	}
	
	public Location getLocation() {
		
		return(location);
	}

	protected void setTimeout(int timeout) {
		
		this.timeout = timeout;
	}
	
	protected int getTimeout() {
	
		return(timeout);
	
	}
}


