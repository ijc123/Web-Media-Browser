package virtualFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Specifies a resource location, which can be either a local
 * path or a remote URL.
 * URL parts are automatically encoded when using the setPath/Filename 
 * and decoded when using the getPath/Filename functions
 * @author Ilja
 * 
 */
public class Location implements Cloneable {

	private URL url;
	private String filepath;
				
	public Location(String location) throws URISyntaxException, MalformedURLException {
				
		if(location.startsWith("file")) {
			
			if(!location.startsWith("file:///")) {
			
				throw new MalformedURLException();
			}
							
			try {
				
				String filepath = URLDecoder.decode(location, "UTF-8");
				filepath = filepath.substring(8);
				setFilepath(filepath);
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		} else if(location.startsWith("ftp") || location.startsWith("http")) {
			
			filepath = null;			
			createURL(location);
					
		} else {
			
			// location is a disk path	
			setFilepath(location);
						
		}
		
	}
		
	
	public String getEncodedURL() {
		
		String urlString = url.toString();
		
		if(url.getProtocol().equals("file")) {
			
			urlString = urlString.replace("file:/", "file:///");
		}
		
		return(urlString);
	}
	
	
	public String getDecodedURL() {
		
		String seperator = "://";
		
		if(url.getProtocol().equals("file")) {
			
			seperator += "/";
		}
		
		String urlString = getProtocol() + seperator + getUserInfo() + 
				getHost() + getPath() + getFilename() + getQuery();
		
		return(urlString);
	}
		
	
	public String getDiskPath() {
		
		if(filepath == null) return(null);
		
		String diskPath = getHost() + getPath() + getFilename();
						
		return(diskPath);
	}
		
	
	public void setUsername(String username) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {
		
		if(url.getProtocol().equals("file")) throw new MalformedURLException();
				
		String userInfo = username + ":" + getPassword();
		
		createURL(url.getProtocol(), userInfo, url.getHost(), url.getPort(),
				url.getPath(), url.getQuery());
				
	}
	
	
	public String getUsername() {
		
		String username = "";
		
		String userInfo = url.getUserInfo();
		
		if(userInfo != null) {
		
			username = userInfo.substring(0, userInfo.indexOf(':'));
		}
		
		return(username);
		
	}
	
	
	public void setPassword(String password) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {
			
		if(url.getProtocol().equals("file")) throw new MalformedURLException();
		
		String userInfo = getUsername() + ":" + password;
		
		createURL(url.getProtocol(), userInfo, url.getHost(), url.getPort(),
				url.getPath(), url.getQuery());
	}

	
	public String getPassword() {
		
		String password = "";
		
		String userInfo = url.getUserInfo();
		
		if(userInfo != null) {
		
			password = userInfo.substring(userInfo.indexOf(':') + 1);
		}
		
		return(password);
	}
	
	
	public String getHost() {
		
		String host;
		
		if(filepath != null) {
					
			host = filepath.substring(0, filepath.indexOf('/'));
			
		} else {
			
			host = url.getHost();
			
			if(url.getProtocol().equals("file")) {
			
				host += ":";
			}
		}
		
		return(host);
		
		
	}
	
	
	public void setHost(String host) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {
		
		if(filepath != null) {
			
			setFilepath(host + getPath() + getFilename()); 
			
		} else {
						
			createURL(url.getProtocol(), url.getUserInfo(), host, 
					url.getPort(), url.getPath(), url.getQuery());
		}
	
	}
	
	
	public void setPath(String path) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {

		if(filepath != null) {
		
			path = path.replace('\\', '/');
			
			if(!path.startsWith("/")) path = "/" + path;
			if(!path.endsWith("/")) path = path + "/";
			
			setFilepath(getHost() + path + getFilename());						
			
		} else {
			
			if(!path.startsWith("/")) path = "/" + path;
			if(!path.endsWith("/")) path = path + "/";
					
			String newPath = path + getFilename();
			
			createURL(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
					newPath, url.getQuery());
		}
	
				
	}
	
	
	public String getPath() {
		
		String path;
		
		if(filepath != null) {
												
			path = filepath.substring(filepath.indexOf('/'), filepath.lastIndexOf('/') + 1);
			
			if(!path.endsWith("/")) path += "/";
			
		} else {
				
			path = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
			
			if(!path.endsWith("/")) path += "/";
			
			try {
				
				path = URLDecoder.decode(path, "UTF-8");
								
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
		}
		
		return(path);
	}
		
	
	public void setFilename(String filename) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {
		
		if(filepath != null) {
			
			setFilepath( getHost() + getPath() + filename );
					
		} else {
					
			String newPath = getPath() + filename;
			
			createURL(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
					newPath, url.getQuery());
		}
	}
	
	
	public String getFilename() {

		String filename;
		
		if(filepath != null) {
			
			filename = filepath.substring(filepath.lastIndexOf('/') + 1);
			
		} else {
		
			String path = url.getPath();
			
			filename = url.getPath().substring(path.lastIndexOf('/') + 1);
			
			try {
				
				filename = URLDecoder.decode(filename, "UTF-8");
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return(filename);
		
	}
	
	
	public void setFilenameWithoutExtension(String filename) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {
				
		if(filepath != null) {
			
			setFilepath( getHost() + getPath() + filename + getExtension() );
											
		} else {
				
			String newPath = getPath() + filename + getExtension();
			
			createURL(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
					newPath, url.getQuery());
		}
	}

	
	public String getFilenameWithoutExtension() {
		
		String filename = getFilename();
				
		int index = filename.lastIndexOf('.');
		
		if(index != -1) {
			
			filename = filename.substring(0, index);
			
		}
		
		return(filename);
	}
	
	
	public void setExtension(String extension) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException  {
	
		if(extension.startsWith(".")) {
			
			extension = extension.substring(1);
		}
		
		String filename = getFilenameWithoutExtension() + "." + extension;
		
		setFilename(filename);
	
	}
	
	
	public String getExtension() {
		
		String filename = getFilename();
		
		String extension = filename.substring(filename.indexOf('.') + 1);
		
		return(extension);
	}
	
	
	public boolean isWithoutFilename() {
		
		return(getFilename().isEmpty());
	}
	
	
	public String getQuery() {
		
		String query = url.getQuery();
		
		if(query != null) {
			
			try {
				query = URLDecoder.decode(query,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return(query);
			
		} else return("");
	}
	
	
	public String getDiskPathWithouthFilename() {
		
		String diskPath = getDiskPath();
		String filename = getFilename();
		
		diskPath = diskPath.substring(0, diskPath.length() - filename.length());
		
		return(diskPath);
	}

	
	public String getPathWithFilename() {

		return(getPath() + getFilename());
			
	}
		
	public String getProtocol() {
	
		return(url.getProtocol());
	}
	
	public boolean moveUp()  {
		
		String curPath = getPath();
		
		int pos = curPath.substring(0, curPath.length() - 1).lastIndexOf("/");
		
		if(pos == -1) return(false);
		
		StringBuffer s = new StringBuffer(curPath);
		
		String newPath = s.delete(pos + 1, curPath.length()).toString();
		
		try {
			setPath(newPath);
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
				
		String newPath = getPath() + downDir;
	
		setPath(newPath);
	}
	
	private String getUserInfo() {
		
		String userInfo = url.getUserInfo();
		
		if(userInfo != null) {
			
			return(userInfo += "@");
			
		} else return("");
	}
		
	private void setFilepath(String filepath) throws MalformedURLException {
		
		this.filepath = filepath.toLowerCase().replace('\\', '/');
		
		File file = new File(filepath);
		this.url = file.toURI().toURL();
		
	}
	
	private void createURL(String url) throws URISyntaxException, MalformedURLException {
		
		// if the url is already encoded, decode it first to make sure
		// it is not encoded twice
		try {
			
			String decodedURL = URLDecoder.decode(url, "UTF-8");
			
			URL temp = new URL(decodedURL);

			createURL(temp.getProtocol(), temp.getUserInfo(), temp.getHost(),
					temp.getPort(), temp.getPath(), temp.getQuery());
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

	private void createURL(String protocol, String userInfo, String host, 
			int port, String path, String query) throws URISyntaxException, UnsupportedEncodingException, MalformedURLException {
/*		
		if(query != null) {
			
			query = URLEncoder.encode(query, "UTF-8");
		}
*/		
		url = new URI(protocol, userInfo , host, port, path, query, null).toURL();
	}
		
	@Override
	public Object clone() {
		
		try {
	
			return super.clone();
				
		} catch(CloneNotSupportedException e) {
	
			return null;
		}
	} 
	
	
}
	


