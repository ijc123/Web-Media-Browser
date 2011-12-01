package beans.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

import database.MediaTable;
import database.SettingsTable;

import fileUtils.FileUtils;

public class PreviewImages {
	
	private String previewRoot;
		
	PreviewImages() {
		
		SettingsTable settings = null;
		
		try {
			
			settings = (SettingsTable) new InitialContext().lookup("java:module/SettingsTable");
			
			previewRoot = settings.getSettings().getPreviewRootDirectory();
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public void buildAll() {
		
		MediaTable mediaTable = null;
		
		try {
			
			mediaTable = (MediaTable) new InitialContext().lookup("java:module/MediaTable");
			
			List<database.MediaItem> mediaList = mediaTable.getAllMedia();
			
			for(int i = 0; i < mediaList.size(); i++) {
				
				build(mediaList.get(i));			
			}
			
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
	protected String getPath(final database.MediaItem inputVideo) {
		
		String hashSource = 
			inputVideo.getFileName() + Long.toString(inputVideo.getSizeBytes());
				
		String hash = Integer.toString( hashSource.hashCode() );
		
		String path = inputVideo.getFileName() + " " + hash;
		
		return(path);
	}
	
	public void build(final database.MediaItem inputMedia) {

		if(!inputMedia.getMimeType().startsWith("video")) return;
		
		String inputPath = inputMedia.getPath();
		String outputPath = getPath(inputMedia);
		
		try {
		
			FileUtils f = new FileUtils(previewRoot);
		
			f.createDirectory(outputPath);
			f.moveDown(outputPath);
		
			String font = "H:/mtn-200808a-win32/cyberbit.ttf";

			String batch = String.format(
					"H:/mtn-200808a-win32/mtn -c 2 -r 32 -w 800 -P -I -f %s -O \"%s\" \"%s\"",
					font, f.getPath(), inputPath);
		
			CommandLine cmdLine = CommandLine.parse(batch);

			DefaultExecutor executor = new DefaultExecutor();
		
			ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
			executor.setWatchdog(watchdog);

			executor.execute(cmdLine);

		} catch (ExecuteException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	public List<String> getSmallPreviewImagesList(final database.MediaItem inputMedia) {
	
		List<String> result = new ArrayList<String>();
		
		if(!inputMedia.getMimeType().startsWith("video")) return(result);
		
		FileUtils f = new FileUtils(previewRoot);
		
		String outputPath = getPath(inputMedia);
		
		f.moveDown(outputPath);
		
		ArrayList<String> previewImagesList = new ArrayList<String>();
		
		f.getDirectoryContents(null, previewImagesList);
		
		if(previewImagesList.size() == 0) return(previewImagesList);
		
		// last element is the large preview image		
		previewImagesList.remove(previewImagesList.size() - 1);
		
		for(int i = 0; i < previewImagesList.size(); i++) {
			
			previewImagesList.set(i, f.getPath() + previewImagesList.get(i));
		}
		
		return(previewImagesList);

	}
	
	public String getLargePreviewImage(final database.MediaItem inputMedia) {
	
		if(!inputMedia.getMimeType().startsWith("video")) return(null);
		
		FileUtils f = new FileUtils(previewRoot);
		
		String outputPath = getPath(inputMedia);
		
		f.moveDown(outputPath);
		
		String fileName = inputMedia.getFileName();
		
		String[] splitPath = FileUtils.splitPath(fileName);
		
		String largePreviewImage = splitPath[2] + "_s.jpg"; 
		
		if(!f.exists(largePreviewImage)) return(null);
		else {
			
			return(f.getPath() + largePreviewImage);
		}
	}
}
