package virtualFile;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;

public class FileInfo {

	private String name;
	private String uri;
	private long sizeBytes;
	private boolean isDirectory;

	public FileInfo(String name, String uri, long sizeBytes, boolean isDirectory) {
		
		this.name = name;
		this.uri = uri;
		this.sizeBytes = sizeBytes; 
		this.isDirectory = isDirectory;
		
	}
	
	public FileInfo(File info) {
		
		name = info.getName();
		sizeBytes = info.length();
		isDirectory = info.isDirectory();
	
		uri = info.toURI().toString();
	}
	
	public FileInfo(FTPFile info, String uri) {
	
		name = info.getName();
		sizeBytes = info.getSize();
		isDirectory = info.isDirectory();

		this.uri = uri;
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

	public String getUri() {
		return uri;
	}


}
