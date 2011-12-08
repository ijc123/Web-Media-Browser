package beans.queryTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import multiTagPicker.MultiTagPickerSupport;
import database.MediaItem;
import database.MediaEJB;

@ViewScoped
@Named
public class QueryTableBean extends MultiTagPickerSupport implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private MediaEJB mediaEJB;
	
	protected Comparator<MediaItem> sortOrder;
	
	private List<MediaItem> mediaList;
	private boolean inRenderResponse;
	
	 static final Comparator<MediaItem> FILENAME_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getFileName().compareTo(b.getFileName());
		 	}
	 };

	 static final Comparator<MediaItem> SIZEBYTES_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		if(a.getSizeBytes() < b.getSizeBytes()) return(-1);
		 		if(a.getSizeBytes() == b.getSizeBytes()) return(0);
		 		else return(1);
		 		
		 	}
	 };
	 
	 static final Comparator<MediaItem> CREATION_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getCreation().compareTo(b.getCreation());
		 	}
	 };
	
	 static final Comparator<MediaItem> MODIFIED_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getModified().compareTo(b.getModified());
		 	}
	 };
	 
	public QueryTableBean() {
				
		sortOrder = FILENAME_ORDER;
		
		mediaList = new ArrayList<MediaItem>();
		
		inRenderResponse = false;
		sortByFileName();
		
		//inRenderResponse = false;
		
	}
				
	public void sortByFileName() {
		
		sortOrder = FILENAME_ORDER;

	}
	
	public void sortBySizeBytes() {
		
		sortOrder = SIZEBYTES_ORDER;
		
	}
	
	public void sortByCreation() {
		
		sortOrder = CREATION_ORDER;
		
	}
	
	public void sortByModified() {
		
		sortOrder = MODIFIED_ORDER;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getMediaList() {
		
		// make sure the data lookup is done once on the first occurrence of a render response phase
		FacesContext context = FacesContext.getCurrentInstance();
		
		PhaseId phase = context.getCurrentPhaseId();
		
		if(phase == PhaseId.RENDER_RESPONSE) {  
		
			if(inRenderResponse == false) {
							
				System.out.println("request database access");
			
				mediaList = (List<MediaItem>)context.getApplication().getExpressionFactory()
				.createValueExpression(context.getELContext(), "#{cc.attrs.mediaList}", List.class).getValue(context.getELContext());
				
				Collections.sort(mediaList, sortOrder);
				
				
				inRenderResponse = true;
			}
								
		} else {
			
			inRenderResponse = false;
		}
		
		return(mediaList);
	
	}
	
	
	public void modifyTag(String mode) {
		
		if(getSelectedTags().isEmpty() || mediaList.isEmpty()) return;

		List<MediaItem> selectedMedia = new ArrayList<MediaItem>();

		for(int i = 0; i < mediaList.size(); i++) {

			if(mediaList.get(i).isSelected()) {

				selectedMedia.add(mediaList.get(i));
				mediaList.get(i).setSelected(false);
			}
		}

		List<String> selectedTags = getSelectedTags();

		for(int i = 0; i < selectedMedia.size(); i++) {

			MediaItem media = selectedMedia.get(i);
			ArrayList<String> mediaTags = media.getTagNames();

			for(int j = 0; j < selectedTags.size(); j++) {

				String name = selectedTags.get(j);

				if(mode.equals("add")) {

					mediaTags.add(name);						
					
				} else {

					mediaTags.remove(name);											
				}
			}
		}

		mediaEJB.updateMedia(selectedMedia);

	}
	
}
