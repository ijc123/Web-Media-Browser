package beans.media;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AjaxBehaviorEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multiTagPicker.MultiTagPickerSupport;
import database.MediaItem;
import database.MediaTable;

public abstract class Taggable extends MultiTagPickerSupport {

	protected MediaItem media;

	protected Taggable() {
		
		media = null;
	}
	
	public void setUri(String uri) {
		
		if(media != null) return;
		
		MediaTable mediaTable = null;
		
		try {
			
			mediaTable = (MediaTable) new InitialContext().lookup("java:module/MediaTable");
				
			media = mediaTable.getMediaByUri(uri);	
			
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
	
		MediaTable mediaTable = null;
		
		try {
			
			mediaTable = (MediaTable) new InitialContext().lookup("java:module/MediaTable");
				
			media.setTagNames(new ArrayList<String>(tags));
			
			mediaTable.updateMedia(media);

			tags = media.getTagNames();
			
		} catch (NamingException e) {
			e.printStackTrace();
		}
				
	}
	


}
