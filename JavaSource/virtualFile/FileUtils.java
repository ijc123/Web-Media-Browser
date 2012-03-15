package virtualFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import database.MediaItem;

public abstract class FileUtils {

	protected Location location;
	
	abstract public boolean createDirectory(final String name) throws IOException;
	abstract public boolean exists(final String name) throws IOException;
	abstract public boolean deleteFile(final String name) throws IOException;
	abstract public boolean deleteDirectory(final String name) throws IOException;
	abstract public boolean renameFile(final String oldName, final String newName) throws IOException;	
	
	abstract public void getDirectoryContents(ArrayList<FileInfo> contents) throws IOException; 
		
		
	protected FileUtils(String location) throws IOException, URISyntaxException {
		
		this.location = new Location(location);
	}
			
	// move a directory up in the tree
	public boolean moveUp() {
		
		String curPath = location.getPath();
		
		int pos = curPath.substring(0, curPath.length() - 1).lastIndexOf("/");
		
		if(pos == -1) return(false);
		
		StringBuffer s = new StringBuffer(curPath);
		
		curPath = s.delete(pos + 1, curPath.length()).toString();
		
		try {
			location.setPath(curPath);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return(true);
	}
	
	// move a directory down in the tree
	public void moveDown(final String directory) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		
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
		
		
	}
	
	public Location getLocation() {
		
		return(location);
	}
/*		
	@Override
	public void setPath(String path) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		
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
	public void setFilename(String filename) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		
		location.setFilename(filename);
	}
	
	@Override
	public void setFilenameWithoutExtension(String filename) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		
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
	public void setExtension(String extension) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
		
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
	public String getDecodedURL() {
		
		return(location.getDecodedURL());
	}

	@Override
	public String getDecodedURLWithoutFilename() {
	
		return(location.getDecodedURLWithoutFilename());	
	}
	
	@Override
	public String getEncodedURL() {
		
		return(location.getEncodedURL());
	}
	
	@Override
	public String getHost() {
	
		return(location.getHost());	
	}
	
	@Override
	public void setHost(String host) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
	
		location.setHost(host);
	}
*/	
}

