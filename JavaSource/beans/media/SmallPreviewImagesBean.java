package beans.media;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import database.MediaItem;

@RequestScoped
@Named
public class SmallPreviewImagesBean {
	
	public List<String> imagesURLList(MediaItem media) {
		
		if(media == null) return(new ArrayList<String>());
		//MediaItem media = getMediaItem();
	
		PreviewImages previewImages = new PreviewImages();
		
		List<String> images = previewImages.getSmallPreviewImagesList(media);
		
		if(images.isEmpty()) {
			
			previewImages.build(media);
			images = previewImages.getSmallPreviewImagesList(media);
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String imageURL = handler.getActionURL(context, "/previewimage");
		
		for(int i = 0; i <  images.size(); i++) {
			
			String previewURL = "";
			
			try {
						
				previewURL = URLEncoder.encode(images.get(i), "UTF-8");
			
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
				previewURL = "";
			}
			
			String fullURL = imageURL + "?id=" + previewURL;
			
			fullURL = fullURL.substring(1);
			fullURL = fullURL.substring(fullURL.indexOf('/'));
			
			images.set(i, fullURL);
		}
				
		return(images);
	}
		
	
}


