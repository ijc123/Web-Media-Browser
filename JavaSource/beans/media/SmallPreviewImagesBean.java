package beans.media;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import database.MediaItem;
import database.MediaPreviewEJB;

@RequestScoped
@Named
public class SmallPreviewImagesBean {
	
	@Inject
	MediaPreviewEJB mediaPreviewEJB;
	
	@Inject
	FacesContext context;
	
	private String createPreviewImageURL(String imageType, String path) {
		
		ViewHandler handler = context.getApplication().getViewHandler();
		String imageURL = handler.getActionURL(context, imageType);
		
		String previewURL;
		
		try {
					
			previewURL = URLEncoder.encode(path, "UTF-8");
		
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
			previewURL = "";
		}
		
		String fullURL = imageURL + "?id=" + previewURL;
		
		fullURL = fullURL.substring(1);
		fullURL = fullURL.substring(fullURL.indexOf('/'));
		
		return(fullURL);
	}
	
	
	public List<String> getVideoPreviewImagesURLList(MediaItem media) {
		
		if(media == null) return(new ArrayList<String>());

		List<String> images = mediaPreviewEJB.getSmallPreviewImagesURLList(media);
		
		for(int i = 0; i < images.size(); i++) {
		
			String fullURL = createPreviewImageURL("/previewimage", images.get(i));
			
			images.set(i, fullURL);
		}
				
		return(images);
	}
		
	public String getThumbnailURL(MediaItem media) {
		
		String fullURL = createPreviewImageURL("/thumbnail", media.getUri());
				
		return(fullURL);
	}
}


