package virtualFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Specifies a resource location, which can be either a local
 * path or a remote URL.
 * URL parts are automatically encoded when using the setPath/Filename 
 * and decoded when using the getPath/Filename functions
 * @author Ilja
 * 
 */
public class Location implements LocationInterface, Cloneable {

	private URL url;
	private String filepath;
				
	public Location(String location) throws MalformedURLException {
			
		if(location.startsWith("file")) {
			
			if(!location.startsWith("file:///")) {
			
				throw new MalformedURLException();
			}
			
			try {
				
				//String fuckyoucunt = URLDecoder.decode("%20", "UTF-8");
				
				String filepath = URLDecoder.decode(location, "UTF-8");
				filepath = filepath.substring(8);
				setFilepath(filepath);
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		} else if(location.startsWith("ftp")) {
			
			filepath = null;
			url = URI.create(location).toURL();
					
		} else {
			
			// location is a disk path	
			setFilepath(location);
						
		}
		
	}
				
	public String getURL() {
		
		String urlString = url.toString();
		
		if(url.getProtocol().equals("file")) {
			
			urlString = urlString.replace("file:/", "file:///");
		}
		
		return(urlString);
	}
	
	public String getLocation() {
		
		String location;
		
		if(filepath != null) {
			
			location = getHost() + getPath() + getFilename();
					
		} else {
		
			String seperator = "://";
			
			if(url.getProtocol().equals("file")) {
				
				seperator += "/";
			}
			
			location = url.getProtocol() + seperator + getUserInfo() + 
					getHost() + getPath() + getFilename() + getQuery();
						
		}
		
		return(location);
	}
		
	public void setUsername(String username) throws MalformedURLException {
		
		if(url.getProtocol().equals("file")) throw new MalformedURLException();
				
		String newURL = url.getProtocol() + "://" + username + ":" + getPassword() + "@" + getHost() + getPath() + getFilename() + getQuery();
		
		url = URI.create(newURL).toURL();
	}
	
	public String getUsername() {
		
		String username = "";
		
		String userInfo = url.getUserInfo();
		
		if(userInfo != null) {
		
			username = userInfo.substring(0, userInfo.indexOf(':'));
		}
		
		return(username);
		
	}
	
	public void setPassword(String password) throws MalformedURLException {
		
		if(url.getProtocol().equals("file")) throw new MalformedURLException();
			
		String newURL = url.getProtocol() + "://" + getUsername() + ":" + password + "@" + getHost() + getPath() + getFilename() + getQuery();
		
		url = URI.create(newURL).toURL();
	}
	
	public String getPassword() {
		
		String password = "";
		
		String userInfo = url.getUserInfo();
		
		if(userInfo != null) {
		
			password = userInfo.substring(userInfo.indexOf(':') + 1);
		}
		
		return(password);
	}
	
	/** 
	 * In case of a local location returns the drive letter
	 * Otherwise the remote host is returned
	 */
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
	
	/** 
	 * In case of a local location sets the drive letter
	 * Otherwise the remote host is set
	 */
	public void setHost(String host) throws MalformedURLException {
		
		if(filepath != null) {
			
			setFilepath(host + getPath() + getFilename()); 
			
		} else {
						
			String newURL = url.getProtocol() + "://" + getUserInfo() + host + getPath() + getFilename() + getQuery();
			
			url = URI.create(newURL).toURL();
		}
	
	}
	
	
	public void setPath(String path) throws MalformedURLException {

		if(filepath != null) {
		
			path = path.replace('\\', '/');
			
			if(!path.startsWith("/")) path = "/" + path;
			if(!path.endsWith("/")) path = path + "/";
			
			setFilepath(getHost() + path + getFilename());						
			
		} else {
			
			if(!path.startsWith("/")) path = "/" + path;
			if(!path.endsWith("/")) path = path + "/";
									
			createURL(path + getFilename());
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
		
	public void setFilename(String filename) throws MalformedURLException {
		
		if(filepath != null) {
			
			setFilepath( getHost() + getPath() + filename );
					
		} else {
						
			createURL(getPath() + filename);
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
	
	public void setFilenameWithoutExtension(String filename) throws MalformedURLException {
				
		if(filepath != null) {
			
			setFilepath( getHost() + getPath() + filename + getExtension() );
											
		} else {
						
			createURL(getPath() + filename + getExtension());
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
	
	public void setExtension(String extension) throws MalformedURLException {
	
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
	
	private String getUserInfo() {
		
		String userInfo = url.getUserInfo();
		
		if(userInfo != null) {
			
			return(userInfo += "@");
			
		} else return("");
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
	
	public String getProtocol() {
	
		return(url.getProtocol());
	}
	
	@Override
	public String getLocationWithoutFilename() {
		
		String location = getLocation();
		String filename = getFilename();
		
		location = location.substring(0, location.length() - filename.length());
		
		return(location);
	}

	@Override
	public String getPathWithFilename() {

		return(getPath() + getFilename());
			
	}
	
	private void setFilepath(String filepath) throws MalformedURLException {
		
		this.filepath = filepath.toLowerCase().replace('\\', '/');
		
		File file = new File(filepath);
		this.url = file.toURI().toURL();
		
	}
	
	private void createURL(String path) throws MalformedURLException {
		
		try {
			
			String scheme = url.getProtocol();
			
			String userInfo = null;
			
			if(!getUsername().isEmpty()) {
				
				userInfo = getUsername() + ":" + getPassword();
			}
			
			String host = url.getHost();
			
			int port = url.getPort();
			
			String query = url.getQuery();
			if(query != null) {
			
				query = URLEncoder.encode(query, "UTF-8");
			}
			
			url = new URI(scheme, userInfo , host, port, path, query, null).toURL();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

