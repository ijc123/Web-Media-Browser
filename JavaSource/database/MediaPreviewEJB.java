package database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.jboss.ejb3.annotation.IgnoreDependency;

import utils.FileUtils;

// Note: Shouldn't really use disk i/o in EJB's
// since files are not a transactional resource
@Stateless
public class MediaPreviewEJB {

	@Inject
	SettingsEJB settings;
	
	@EJB
	@IgnoreDependency
	MediaEJB mediaEJB;

	private String previewRoot;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init(){

		previewRoot = settings.getSettings().getPreviewRootDirectory();
	}

	private String getMediaPreviewPath(final MediaItem inputVideo) {

		String hashSource = 
				inputVideo.getFileName() + Long.toString(inputVideo.getSizeBytes());

		String hash = Integer.toString( hashSource.hashCode() );

		String path = inputVideo.getFileName() + " " + hash;

		return(path);
	}

	public void build(final MediaItem inputMedia) {

		if(inputMedia.isVideo()) {
			
			buildVideoPreviewImages(inputMedia);
		
		} else if(inputMedia.isImage()) {
			
			buildImageThumbnail(inputMedia);
			
		}

	}
	
	
	public void buildImageThumbnail(final MediaItem inputMedia) {
		
		if(mediaEJB.hasMediaThumbnail(inputMedia)) return;
		
		mediaEJB.storeMediaThumbnail(inputMedia);	
							
	}
	
	private void buildVideoPreviewImages(final MediaItem inputMedia) {
		
		if(!getSmallPreviewImagesList(inputMedia).isEmpty()) {

			// preview images already exist
			return;
		}

		String inputPath = inputMedia.getPath();
		String outputPath = getMediaPreviewPath(inputMedia);

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

	public List<String> getSmallPreviewImagesList(final MediaItem inputMedia) {

		List<String> result = new ArrayList<String>();

		if(!inputMedia.getMimeType().startsWith("video")) return(result);

		FileUtils f = new FileUtils(previewRoot);

		String outputPath = getMediaPreviewPath(inputMedia);

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

	public String getLargePreviewImage(final MediaItem inputMedia) {

		if(!inputMedia.getMimeType().startsWith("video")) return(null);

		FileUtils f = new FileUtils(previewRoot);

		String outputPath = getMediaPreviewPath(inputMedia);

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
