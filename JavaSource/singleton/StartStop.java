package singleton;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import debug.Log;

import video.HTTPLiveStreaming;
import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;

@Startup
@Singleton
public class StartStop {

	@PostConstruct 
	void atStartup() {

		// remove lingering transcoded video files
		try {
			
			FileUtils f = FileUtilsFactory.create(HTTPLiveStreaming.getOutputPath());
			
			Log.info(this, "Deleting *.ts and *.m3u8 files in: " + f.getLocation().getDecodedURL());
						
			ArrayList<FileInfo> tsFiles = new ArrayList<FileInfo>();

			f.getDirectoryContents(tsFiles, "*.ts");
			
			for(int i = 0; i < tsFiles.size(); i++) {

				f.deleteFile(tsFiles.get(i).getName());
			}
			
			ArrayList<FileInfo> indexFiles = new ArrayList<FileInfo>();
			
			f.getDirectoryContents(indexFiles, "*.m3u8");
			
			for(int i = 0; i < indexFiles.size(); i++) {

				f.deleteFile(indexFiles.get(i).getName());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@PreDestroy
	void atShutdown() { 

	}

}

