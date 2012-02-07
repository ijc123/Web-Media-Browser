package virtualFile;


public class LocationLocal extends Location {

	private String drive;
	
	public LocationLocal(String location) {
		
		super();
	
		splitLocation(location);
				
	}
	
	private void splitLocation(String location) {
			
		int drivePos = location.indexOf('/');
		int dotPos = location.lastIndexOf('.');
		int sepPos = location.lastIndexOf('/');
		
		if(drivePos != -1) {
			
			setDrive(location.substring(0, drivePos));
			
			if(sepPos != -1) {
			
				setPath(location.substring(drivePos, sepPos));
			}
		}
		
		if(dotPos != -1) {
					
			setFilename(location.substring(sepPos + 1, dotPos));						
			setExtension(location.substring(dotPos + 1));
		}	
	
	}
	
	
	@Override
	public String getLocation() {
		
		String location = drive + getPathWithFilename();
		
		return(location);
	}

	@Override
	public String getLocationWithoutFilename() {
		
		String location = drive + getPath();
		
		return(location);
	}
	
	public String getDrive() {
		return drive;
	}

	public void setDrive(String drive) {
		this.drive = drive;
	}



	
}
