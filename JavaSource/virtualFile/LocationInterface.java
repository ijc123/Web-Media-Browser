package virtualFile;

import java.net.MalformedURLException;

public interface LocationInterface {

	public String getLocation();

	public String getLocationWithoutFilename();

	public String getURL();
	
	public String getHost();
	public void setHost(String host) throws MalformedURLException;
	
	public void setPath(String path) throws MalformedURLException;

	public String getPath();

	public String getPathWithFilename();

	public void setFilename(String filename) throws MalformedURLException;

	public void setFilenameWithoutExtension(String filename)
			throws MalformedURLException;

	public String getFilenameWithoutExtension();

	public String getFilename();

	public void setExtension(String extension) throws MalformedURLException;

	public String getExtension();

	public boolean isWithoutFilename();

}
