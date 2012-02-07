package virtualFile;

public class LocationFactory {

	static public Location create(String uri) {
		
		Location location;
		
		if(uri.startsWith("ftp://")) {
			
			location = new LocationRemote(uri);
			
		} else {
			
			location = new LocationLocal(uri);
		}
		
		return(location);
	}
	
	static public Location create(String uri, String username, String password) {
		
		Location location = new LocationRemote(uri, username, password);
				
		return(location);
	}
}
