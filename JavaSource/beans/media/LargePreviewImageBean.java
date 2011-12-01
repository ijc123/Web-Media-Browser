package beans.media;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import com.google.gson.Gson;

@ManagedBean
@ViewScoped
public class LargePreviewImageBean extends Taggable {
		
	private String largeImagePath;
	private List<String> smallImagesPath;
	private String previewURL;

	public LargePreviewImageBean() {
		
		largeImagePath = null;
		smallImagesPath = null;
		previewURL = null;
	
	}
	
	@Override
	public void setUri(String uri) {
				
		super.setUri(uri);
		
		PreviewImages previewImages = new PreviewImages();
		
		largeImagePath = previewImages.getLargePreviewImage(media);
		smallImagesPath = previewImages.getSmallPreviewImagesList(media);
		
		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String imageURL = handler.getActionURL(context, "/previewimage");
	
		previewURL = "";
		
		try {
					
			previewURL = URLEncoder.encode(largeImagePath, "UTF-8");
			
			String fullURL = imageURL + "?id=" + previewURL;
			
			fullURL = fullURL.substring(1);
			fullURL = fullURL.substring(fullURL.indexOf('/'));
			
			previewURL = fullURL;
		
		
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
	
		}
		
	}

		
	public void setPreviewURL(String previewURL) {
			

	}

	public String getPreviewURL() {
		
		return(previewURL);
	}

	public String getFileName() {
		return(media.getFileName());
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
