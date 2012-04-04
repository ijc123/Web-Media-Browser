package beans.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import servlet.LoadMediaServlet;

import com.google.gson.Gson;

import database.MediaPreviewEJB;

@ViewScoped
@Named
public class LargePreviewImageBean extends PlayMediaBean implements Serializable {
	
	@Inject
	MediaPreviewEJB mediaPreviewEJB;
	
	private static final long serialVersionUID = 1L;
	private String largeImagePath;
	private List<String> smallImagesPath;
	private String previewURL;

	public LargePreviewImageBean() {
		
		largeImagePath = null;
		smallImagesPath = null;
		previewURL = null;
	
	}
	
	@Override
	public void setMediaId(int id) {
				
		super.setMediaId(id);
		
		if(media.isVideo()) {
		
			largeImagePath = mediaPreviewEJB.getLargePreviewImageURL(media);
			smallImagesPath = mediaPreviewEJB.getSmallPreviewImagesURLList(media);
		
		} else if(media.isImage()) {
			
			largeImagePath = media.getPath();
		}
		
		previewURL = LoadMediaServlet.getMediaDataURL(media);
				
		int index = previewURL.substring(1).indexOf('/');
		
		previewURL = previewURL.substring(index + 1);
	
	}

		
	public void setPreviewURL(String previewURL) {
			

	}

	public String getPreviewURL() {
		
		return(previewURL);
	}

	
	public String getImageInfo() {
				
		if(largeImagePath == null) return("");
		if(smallImagesPath == null) return("");
		if(smallImagesPath.size() == 0) return("");
		
		File []input = {new File(largeImagePath), new File(smallImagesPath.get(0))};
				
		// grab image dimensions
		List<String> imageInfo = new ArrayList<String>();
		
		try {
			
			for(int i = 0; i < 2; i++) {
			
				BufferedImage image = ImageIO.read(input[i]);
			
				imageInfo.add(Integer.toString( image.getWidth() ));
				imageInfo.add(Integer.toString( image.getHeight() ));
			}
						
		} catch (IOException e) {

			e.printStackTrace();
		}
				
		
		// grab snapshot times in seconds
		Pattern p = Pattern.compile("_");

		for(int i = 0; i < smallImagesPath.size(); i++) {
			
			 String[] result = p.split(smallImagesPath.get(i));
			 
			 if(result.length < 4) continue;
			 
			 int seconds = Integer.parseInt( result[result.length - 2] );
			 int minutes =  Integer.parseInt( result[result.length - 3] );
			 int hours =  Integer.parseInt( result[result.length - 4] );
			
			 Integer totalSeconds = (hours * 60 + minutes) * 60 + seconds;
			 
			 imageInfo.add(totalSeconds.toString());
			
		}
	
		Gson gson = new Gson();
		
		return gson.toJson(imageInfo);
	}

}
