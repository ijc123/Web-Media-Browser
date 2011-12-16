package beans.queryTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import multiTagPicker.MultiTagPickerSupport;

import org.richfaces.event.ItemChangeEvent;
import org.richfaces.event.ItemChangeListener;

import database.MediaEJB;
import database.MediaItem;
import database.MediaPreviewEJB;

@ViewScoped
@Named
public class QueryTableBean extends MultiTagPickerSupport 
	implements Serializable, ItemChangeListener 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private MediaEJB mediaEJB;
	
	@Inject
	private MediaPreviewEJB mediaPreviewEJB;
		
	private boolean inRenderResponse;
	
	private List<MediaTableItem> mediaList;
	private DataModel<MediaTableItem> queryTable;
	
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
	 
	 static final Comparator<MediaItem> VERSION_ORDER =
			 
	         new Comparator<MediaItem>() {
			 
			 	public int compare(MediaItem a, MediaItem b) {
			 		
			 		if(a.getVersion() < b.getVersion()) return(-1);
			 		if(a.getVersion() == b.getVersion()) return(0);
			 		else return(1);
			 	}
	 };
	 
	public QueryTableBean() {
		
		mediaList = new ArrayList<MediaTableItem>();
						
		inRenderResponse = false;
		sortByFileName();
		
		
	}
				
	public void sortByFileName() {
		
		Collections.sort(mediaList, FILENAME_ORDER);
	}
	
	public void sortBySizeBytes() {
		
		Collections.sort(mediaList, SIZEBYTES_ORDER);			
	}
	
	public void sortByCreation() {
		
		Collections.sort(mediaList, CREATION_ORDER);
	}
	
	public void sortByModified() {
		
		Collections.sort(mediaList, MODIFIED_ORDER);
	}
	
	public void sortByVersion() {
		
		Collections.sort(mediaList, VERSION_ORDER);		
	}
		
	public void setMediaList(List<MediaItem> media) {
		
		mediaList = new ArrayList<MediaTableItem>();
		
		for(int i = 0; i < media.size(); i++) {
			
			mediaList.add(new MediaTableItem(media.get(i)));
			
		}
		
		sortByFileName();
	}
	
	public DataModel<MediaTableItem> getQueryTableModel() {
		
		// make sure the data lookup is done once on the first occurrence of a render response phase
		FacesContext context = FacesContext.getCurrentInstance();
		
		PhaseId phase = context.getCurrentPhaseId();
		
		if(phase == PhaseId.RENDER_RESPONSE) {  
		
			if(inRenderResponse == false) {									
							
//				mediaList = (List<MediaItem>)context.getApplication().getExpressionFactory()
//				.createValueExpression(context.getELContext(), "#{cc.attrs.mediaList}", List.class).getValue(context.getELContext());								
																					
				queryTable = new ListDataModel<MediaTableItem>(mediaList);				
				
				inRenderResponse = true;
			}
								
		} else {
			
			inRenderResponse = false;
		}
		
		return(queryTable);
	
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
	
	// detect changing tab panels in mediaInfo 
	@Override
	public void processItemChange(ItemChangeEvent event)
			throws AbortProcessingException {
		
		 int rowIndex = queryTable.getRowIndex();
		 
		 MediaTableItem mediaTableItem = mediaList.get(rowIndex);
		 
		 // in case we switch to the preview tab make sure there
		 // is a preview image
		 if(event.getNewItemName().equals("preview")) {
			 			 
			 mediaPreviewEJB.build(mediaTableItem);
		 }
		 
		 // update the mediaitem with the latest version from the database				
		 mediaTableItem.setMediaItemData(mediaEJB.getMediaByUri(mediaTableItem.getUri()));
		 
		 mediaTableItem.setSelectedTab(event.getNewItemName());
		 
		 mediaList.set(rowIndex, mediaTableItem);
		
	}
	

	
}
