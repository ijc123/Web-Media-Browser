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
	
	public void setMediaId(int mediaId) {
		
		if(media != null) return;
		
		MediaEJB mediaEJB = null;
		
		try {
			
			mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
				
			media = mediaEJB.getMediaById(mediaId);	
			
		} catch (NamingException e) {
			
			e.printStackTrace();
		}
				
	}
	
	public int getMediaId() {
		
		if(media == null) {
			
			return(-1);
		
		} else {
			
			return(media.getId());
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
