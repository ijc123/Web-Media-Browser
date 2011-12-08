package beans.media;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AjaxBehaviorEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multiTagPicker.MultiTagPickerSupport;
import database.MediaItem;
import database.MediaEJB;

public abstract class Taggable extends MultiTagPickerSupport {

	protected MediaItem media;

	protected Taggable() {
		
		media = null;
	}
	
	public void setUri(String uri) {
		
		if(media != null) return;
		
		MediaEJB mediaEJB = null;
		
		try {
			
			mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
				
			media = mediaEJB.getMediaByUri(uri);	
			
		} catch (NamingException e) {
			
			e.printStackTrace();
		}
				
	}
	
	public String getUri() {
		
		if(media == null) {
			
			return("");
		
		} else {
			
			return(media.getUri());
		}
		
	}
	
	@Override
	public List<String> getSelectedTags() {

		if(tags == null) {
		
			tags = media.getTagNames();
		}
		
		return(tags);
	}
	
	public void replaceTagsEvent(AjaxBehaviorEvent event)  {
	
		MediaEJB mediaEJB = null;
		
		try {
			
			mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
				
			media.setTagNames(new ArrayList<String>(tags));
			
			mediaEJB.updateMedia(media);

			tags = media.getTagNames();
			
		} catch (NamingException e) {
			e.printStackTrace();
		}
				
	}
	


}
