package beans.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multiTagPicker.MultiTagPickerSupport;
import database.MediaItem;
import database.MediaEJB;

@ViewScoped
@Named
public class PlayMediaBean extends MultiTagPickerSupport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected MediaItem media;

	public PlayMediaBean() {
		
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
	
	public String getFileName() {
		
		return(media.getFileName());
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
