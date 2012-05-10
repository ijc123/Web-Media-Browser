package beans.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import servlet.LoadResourceServlet;
import virtualFile.Location;

import com.google.gson.Gson;

import database.MediaPreviewEJB;

@ViewScoped
@Named
public class LargePreviewImageBean extends PlayMediaBean implements Serializable {
	
	@Inject
	MediaPreviewEJB mediaPreviewEJB;
	
	private static final long serialVersionUID = 1L;
	private Location largeImageLocation;
	private List<Location> smallImageLocation;
	private String previewURL;

	public LargePreviewImageBean() {
		
		largeImageLocation = null;
		smallImageLocation = null;
		previewURL = null;
	
	}
	
	@Override
	public void setMediaId(int id) {
				
		super.setMediaId(id);
			
		try {
			
			if(media.isVideo()) {
			
				largeImageLocation = new Location(mediaPreviewEJB.getLargePreviewImageURL(media));
				
				List<String> urlList = mediaPreviewEJB.getSmallPreviewImagesURLList(media);
				
				smallImageLocation = new ArrayList<Location>();
				
				for(int i = 0; i < urlList.size(); i++) {
				
					smallImageLocation.add(new Location(urlList.get(i)));
				}
			} 
			
			previewURL = LoadResourceServlet.getMediaPreviewURL(media);
					
			int index = previewURL.substring(1).indexOf('/');
			
			previewURL = previewURL.substring(index + 1);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	
	}

		
	public void setPreviewURL(String previewURL) {
			

	}

	public String getPreviewURL() {
		
		return(previewURL);
	}

	
	public String getImageInfo() {
				
		if(largeImageLocation == null) return("");
		if(smallImageLocation == null) return("");
		if(smallImageLocation.size() == 0) return("");
		
		File []input = {new File(largeImageLocation.getDiskPath()), new File(smallImageLocation.get(0).getDiskPath())};
				
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

		for(int i = 0; i < smallImageLocation.size(); i++) {
			
			 String[] result = p.split(smallImageLocation.get(i).getDiskPath());
			 
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
