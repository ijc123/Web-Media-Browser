package database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import utils.ImagePreviewBuilder;
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

		String outputPath = getMediaPreviewPath(inputMedia);
		
		FrameGrabber frameGrabber = new FrameGrabber();
		ImagePreviewBuilder previewBuilder = new ImagePreviewBuilder();
		
		try {

			FileUtilsLocal f = new FileUtilsLocal(previewRoot);

			f.createDirectory(outputPath);
			f.moveDown(outputPath);
			
			Location inputLocation = new Location(inputMedia.getUri());
						
			f.setFilename(inputLocation.getFilename());
			
			Location outputLocation = new Location(f.getURL());
							
			frameGrabber.start(inputLocation, 400, outputLocation, 60);
			previewBuilder.start(outputLocation);

		} catch (Exception e) {
			// TODO Auto-generated catch block
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
				
				f.setFilename(imageFile.getName());
				
				previewImagesPath.add(f.getLocation());
			}

		} catch (IOException e) {
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
			
			f.setFilename(inputMedia.getFileName());
		
			String newFilename = f.getFilenameWithoutExtension() + "_s";
			
			f.setFilenameWithoutExtension(newFilename);
			f.setExtension("jpg");
			
			if(!f.exists(f.getFilename())) return(null);
			else {

				return(f.getLocation());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return(null);
		}


	}

	

}
