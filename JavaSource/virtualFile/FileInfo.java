package virtualFile;

import java.net.MalformedURLException;

public class FileInfo {

	private String name;
	private Location location;
	private long sizeBytes;
	private boolean isDirectory;

	public FileInfo(String name, Location location, long sizeBytes, boolean isDirectory) throws MalformedURLException {
		
		this.name = name;
		this.location = location;
		this.sizeBytes = sizeBytes; 
		this.isDirectory = isDirectory;
		
	}
	
	public String getName() {
		return name;
	}
	
	public long getSizeBytes() {
		return sizeBytes;
	}
	
	public boolean isDirectory() {
		return isDirectory;
	}

	public Location getLocation() {
		return location;
	}


}
