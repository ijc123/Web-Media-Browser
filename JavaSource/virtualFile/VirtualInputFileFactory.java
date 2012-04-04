package virtualFile;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;

public class VirtualInputFileFactory {

	static public VirtualInputFile create(String location) throws SocketException, IOException, URISyntaxException {
		
		VirtualInputFile inputFile = null;
		
		if(location.startsWith("ftp://")) {
			
			inputFile = new RemoteInputFile(new Location(location));
			
		} else {
			
			inputFile = new LocalInputFile(new Location(location));
		}
		
		return(inputFile);
	}
	
	static public VirtualInputFile create(final Location location) throws IOException, URISyntaxException {
		
		VirtualInputFile fileUtils = null;
		
		if(location.getProtocol().equals("file")) {
			
			fileUtils = new LocalInputFile((Location)location.clone());
			
		} else {
						
			fileUtils = new RemoteInputFile((Location)location.clone());
		}
		
		return(fileUtils);
	}
}
