package virtualFile;

public abstract class Location implements LocationInterface {

	private String path;
	private String filename;
	private String extension;
	
	abstract public String getLocation();
	abstract public String getLocationWithoutFilename();
	
	public Location() {
		
		path = "";
		filename = "";
		extension = "";
	}
	
	public void setPath(String path) {
		
		if(!path.startsWith("/")) path = "/" + path;
		if(!path.endsWith("/")) path = path + "/";		
		
		this.path = path;
				
	}
	
	public String getPath() {
		
		return(path);
	}
	
	public String getPathWithFilename() {
				
		return(getPath() + getFilename());
	}
	
	public void setFilename(String filename) {
			
		if(filename == null) return;
		
		int dotPos = filename.lastIndexOf('.');
		
		if(dotPos != -1) {
			
			this.filename = filename.substring(0, dotPos);						
			setExtension(filename.substring(dotPos + 1));
			
		} else {
			
			this.filename = filename;
		}
		
	}
	
	public void setFilenameWithoutExtension(String filename) {
		
		this.filename = filename;
	}
	
	public String getFilenameWithoutExtension() {
		
		return(filename);
	}
	
	public String getFilename() {
		
		if(extension.isEmpty()) {
			
			return(filename);
			
		} else {
			
			return(filename + "." + extension);
		}
		
	}
	
	public void setExtension(String extension) {
		
		if(extension.startsWith(".")) {
			
			extension = extension.substring(1);
		}
		
		this.extension = extension;
	}
	
	public String getExtension() {
		
		return(extension);
	}
	
	public boolean isWithoutFilename() {
		
		return(!getFilename().isEmpty());
	}
	
}
