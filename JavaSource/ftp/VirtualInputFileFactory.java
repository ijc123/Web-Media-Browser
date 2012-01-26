package ftp;

import java.io.IOException;
import java.net.SocketException;

public class VirtualInputFileFactory {

	static public VirtualInputFile create(String location) throws SocketException, IOException {
		
		VirtualInputFile inputFile;
		
		if(location.startsWith("ftp://")) {
			
			inputFile = new RemoteInputFile(location);
			
		} else {
			
			inputFile = new LocalInputFile(location);
		}
		
		return(inputFile);
	}
}
