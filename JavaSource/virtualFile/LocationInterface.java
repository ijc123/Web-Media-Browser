package virtualFile;

public interface LocationInterface {

	public String getLocation();
	public String getLocationWithoutFilename();
	
	public void setPath(String path);
	
	public String getPath();
	
	public String getPathWithFilename();
	
	public void setFilename(String filename);
	
	public void setFilenameWithoutExtension(String filename);
	public String getFilenameWithoutExtension();
	public String getFilename();
	
	public void setExtension(String extension);
	
	public String getExtension(); 
	public boolean isWithoutFilename();
	
	
}
