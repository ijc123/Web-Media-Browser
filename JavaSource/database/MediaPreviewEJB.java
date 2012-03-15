package database;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import virtualFile.FileUtilsLocal;
import virtualFile.Location;

// Note: Shouldn't really use disk i/o in EJB's
// since files are not a transactional resource
@Stateless
public class MediaPreviewEJB {

	@Inject
	SettingsEJB settings;
	
	@EJB
	//@IgnoreDependency
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

		if (!getSmallPreviewImagesList(inputMedia).isEmpty()) {

			// preview images already exist
			return;
		}

		String inputPath = inputMedia.getPath();
		String outputPath = getMediaPreviewPath(inputMedia);

		try {

			FileUtilsLocal f = new FileUtilsLocal(previewRoot);

			f.createDirectory(outputPath);
			f.moveDown(outputPath);

			
			String batch = String
					.format("H:/mtn-200808a-win32/mtn -c 2 -r 32 -w 800 -P -I -f %s -O \"%s\" \"%s\"",
							f.getLocation().getDiskPath(), inputPath);

			CommandLine cmdLine = CommandLine.parse(batch);

			DefaultExecutor executor = new DefaultExecutor();

			ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
			executor.setWatchdog(watchdog);
			

			executor.execute(cmdLine);

		} catch (Exception e) {

			e.printStackTrace();

		} 
	}

	public List<String> getSmallPreviewImagesList(final MediaItem inputMedia) {

		
		ArrayList<String> previewImagesPath = new ArrayList<String>();

		try {

			ArrayList<FileInfo> previewImagesList = new ArrayList<FileInfo>();
			
			List<String> result = new ArrayList<String>();

			if(!inputMedia.getMimeType().startsWith("video")) return(result);

			FileUtils f = FileUtilsFactory.create(previewRoot);

			String outputPath = getMediaPreviewPath(inputMedia);

			f.moveDown(outputPath);

			f.getDirectoryContents(previewImagesList, "*.png");

			if(previewImagesList.size() == 0) return(previewImagesPath);
			
			for(int i = 0; i < previewImagesList.size(); i++) {

				FileInfo imageFile = previewImagesList.get(i);
				
				Location imageLocation = (Location)f.getLocation().clone();
				
				imageLocation.setFilename(imageFile.getName());
				
				previewImagesPath.add(imageLocation.getEncodedURL());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return(previewImagesPath);
	}

	public String getLargePreviewImage(final MediaItem inputMedia) {

		if(!inputMedia.getMimeType().startsWith("video")) return(null);

		try {
			
			FileUtils f = FileUtilsFactory.create(previewRoot);

			String outputPath = getMediaPreviewPath(inputMedia);

			f.moveDown(outputPath);
			
			Location outputLocation = (Location)f.getLocation().clone();
			
			outputLocation.setFilename(inputMedia.getFileName());
		
			String newFilename = outputLocation.getFilenameWithoutExtension() + "_s";
			
			outputLocation.setFilenameWithoutExtension(newFilename);
			outputLocation.setExtension("jpg");
			
			if(!f.exists(outputLocation.getFilename())) return(null);
			else {

				return(outputLocation.getEncodedURL());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return(null);
		}


	}

	

}
