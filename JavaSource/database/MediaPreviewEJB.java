package database;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import servlet.LoadDataSegmentServlet;
import video.FrameGrabber;
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

	public void generatePreview(final MediaItem inputMedia) {

		if(inputMedia.isVideo()) {
			
			buildVideoPreviewImages(inputMedia);
		
		} else if(inputMedia.isImage()) {
			
			buildImageThumbnail(inputMedia);
			
		}

	}
	
	public static String getMediaPreviewPath(final MediaItem media) {

		return(Integer.toString(media.getId()));
	}


	public void buildImageThumbnail(final MediaItem inputMedia) {
		
		if(mediaEJB.hasMediaThumbnail(inputMedia)) return;
		
		mediaEJB.storeMediaThumbnail(inputMedia);	
							
	}
	
	private void buildVideoPreviewImages(final MediaItem inputMedia) {

		if(!getSmallPreviewImagesURLList(inputMedia).isEmpty()) {

			// preview images already exist
			return;
		}
		
		String outputPath = getMediaPreviewPath(inputMedia);
		
		try {
			
			FileUtilsLocal f = new FileUtilsLocal(previewRoot);

			f.createDirectory(outputPath);
			f.moveDown(outputPath);
		
			Location outputLocation = f.getLocation();
			
			outputLocation.setFilename(inputMedia.getFileName());
			
			FrameGrabber frameGrabber = new FrameGrabber();
			
			LoadDataSegmentServlet.profiler.startCounter(0, "Overall");
			
			frameGrabber.start(inputMedia, outputLocation);
			
			LoadDataSegmentServlet.profiler.stopCounter(0, "Overall");
			
			LoadDataSegmentServlet.profiler.printResults(true, true);
							
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
	}
	
	public List<String> getSmallPreviewImagesURLList(final MediaItem inputMedia) {
		
		ArrayList<String> previewImagesPath = new ArrayList<String>();

		FileUtils f = null;
		
		try {

			ArrayList<FileInfo> previewImagesList = new ArrayList<FileInfo>();
			
			List<String> result = new ArrayList<String>();

			if(!inputMedia.getMimeType().startsWith("video")) return(result);

			f = FileUtilsFactory.create(previewRoot);

			String outputPath = getMediaPreviewPath(inputMedia);

			f.moveDown(outputPath);

			f.getDirectoryContents(previewImagesList, "*.jpg");
			
			for(int i = 1; i < previewImagesList.size(); i++) {

				FileInfo imageFile = previewImagesList.get(i);
				
				Location imageLocation = (Location)f.getLocation().clone();
				
				imageLocation.setFilename(imageFile.getName());
				
				previewImagesPath.add(imageLocation.getEncodedURL());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			
			if(f != null) {
				
				f.close();
			}
			
		}

		return(previewImagesPath);
	}

	public String getLargePreviewImageURL(final MediaItem inputMedia) {

		if(!inputMedia.getMimeType().startsWith("video")) return(null);

		try {
			
			Location location = new Location(previewRoot);
			
			String previewPath = getMediaPreviewPath(inputMedia);

			location.moveDown(previewPath);
			
			location.setFilename(inputMedia.getFileName());
		
			location.setExtension("jpg");
			
			return(location.getEncodedURL());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return(null);

	}

	

}
