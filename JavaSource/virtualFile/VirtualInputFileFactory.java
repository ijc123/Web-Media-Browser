package virtualFile;

import java.io.IOException;
import java.net.SocketException;

public class VirtualInputFileFactory {

	static public VirtualInputFile create(String location) throws SocketException, IOException {
		
		VirtualInputFile inputFile;
		
		if(location.startsWith("ftp://")) {
			
			inputFile = new RemoteInputFile(new Location(location));
			
		} else {
			
			inputFile = new LocalInputFile(new Location(location));
		}
		
		return(inputFile);
	}
}
