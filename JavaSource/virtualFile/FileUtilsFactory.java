package virtualFile;


public class FileUtilsFactory {

	static public FileUtils create(String location) {
		
		FileUtils fileUtils;
		
		if(location.startsWith("ftp://")) {
			
			fileUtils = new FileUtilsRemote(location);
			
		} else {
			
			fileUtils = new FileUtilsLocal(location);
		}
		
		return(fileUtils);
	}
	
}
