package virtualFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


public class FileUtilsFactory {

	static public FileUtils create(String location) throws IOException, URISyntaxException {
		
		FileUtils fileUtils;
		
		if(location.startsWith("ftp://")) {
			
			fileUtils = new FileUtilsRemote(location);
			
		} else {
			
			fileUtils = new FileUtilsLocal(location);
		}
		
		return(fileUtils);
	}
		
	static public FileUtils create(Location location) throws IOException, URISyntaxException {
		
		FileUtils fileUtils = null;
		
		if(!location.getProtocol().equals("file")) {
			
			try {
				fileUtils = new FileUtilsRemote(location.getEncodedURL());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			
			try {
				fileUtils = new FileUtilsLocal(location.getEncodedURL());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return(fileUtils);
	}

}
