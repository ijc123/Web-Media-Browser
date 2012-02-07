package virtualFile;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class LocationRemote extends Location {

	private String username;
	private String password;
	private String host;
	private int port;
		
	public LocationRemote(String location) {
		
		super();
		
		parseURI(location);
	}
	
	public LocationRemote(String location, String username, String password) {
		
		super();
		
		parseURI(location);
		
		this.username = username;
		this.password= password;
	}
	
	private void parseURI(String location) {
		
		URL url;
		
		try {
				
			url = new URL(location);
			
			//uri = new URI(location);
						
			setHost(url.getHost());			
			setPath(url.getPath());		
			setFilename(url.getQuery());									
			setPort(url.getPort());		
			
			String userInfo = url.getUserInfo();
			
			if(userInfo != null) {
				
				int seperator = userInfo.indexOf(':');
				
				setUsername(userInfo.substring(0, seperator));
				setPassword(userInfo.substring(seperator + 1));
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String getLocation() {
	
		String location = "ftp://";
		
		location += getUserInfoString() + getHost();
				
		if(port != -1) {
			
			location += ":" + Integer.toString(port);
		}
				
		location += getPath() + getFilename();
		
		return(location);

	}
	
	@Override
	public String getLocationWithoutFilename() {
		
		String location = "ftp://";
			
		location += getUserInfoString() + getHost();
				
		if(port != -1) {
			
			location += ":" + Integer.toString(port);
		}
				
		location += getPath();
		
		return(location);
		
	}
	
	public String getLocationWithoutUserInfo() {
		
		String location = "ftp://";
		
		location += getHost();
				
		if(port != -1) {
			
			location += ":" + Integer.toString(port);
		}
				
		location += getPath() + getFilename();
		
		return(location);
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private String getUserInfoString() {
		
		String userInfo = username;
		
		if(!password.isEmpty()) {
			
			userInfo += ":" + password + "@";
		
		} else if(!username.isEmpty()) {
			
			userInfo += "@";
		}
					
		return(userInfo);
	}

}
