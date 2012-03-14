package virtualFile;

import java.net.MalformedURLException;


public class FileUtilsFactory {

	static public FileUtils create(String location) throws MalformedURLException {
		
		FileUtils fileUtils;
		
		if(location.startsWith("ftp://")) {
			
			fileUtils = new FileUtilsRemote(location);
			
		} else {
			
			fileUtils = new FileUtilsLocal(location);
		}
		
		return(fileUtils);
	}
		
	static public FileUtils create(Location location) {
		
		FileUtils fileUtils = null;
		
		if(!location.getProtocol().equals("file")) {
			
			try {
				fileUtils = new FileUtilsRemote(location.getURL());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			
			try {
				fileUtils = new FileUtilsLocal(location.getURL());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return(fileUtils);
	}

}
