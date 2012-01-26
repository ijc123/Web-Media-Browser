package utils;

public class FileInfo {

	private String path;
	private String uri;
	private String fileName;
	private long sizeBytes; 
	private boolean isDirectory;
	private String mimeType;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
		
		if(isDirectory == false) {
			
			mimeType = MimeType.getMimeTypeFromExt(fileName);

		} else {
			
			mimeType = null;
		}
	}
	public long getSizeBytes() {
		return sizeBytes;
	}
	public void setSizeBytes(long l) {
		this.sizeBytes = l;
	}	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public void setIsDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	public boolean isDirectory() {
		return isDirectory;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getMimeType() {
		return mimeType;
	}
	
}
