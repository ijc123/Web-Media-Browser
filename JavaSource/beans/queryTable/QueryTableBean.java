package beans.queryTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
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
	
	@Inject
	private FacesContext context;
			
	private List<MediaTableItem> mediaList;
	private DataModel<MediaTableItem> queryTable;
	private Comparator<MediaItem> currentSortMode;
	
	
	private Comparator<MediaItem> FILENAME_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	@Override
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getFileName().compareTo(b.getFileName());
		 	}
	 };

	 private Comparator<MediaItem> SIZEBYTES_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	@Override
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		if(a.getSizeBytes() < b.getSizeBytes()) return(-1);
		 		if(a.getSizeBytes() == b.getSizeBytes()) return(0);
		 		else return(1);
		 		
		 	}
	 };
	 
	 private Comparator<MediaItem> CREATION_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	@Override
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getCreation().compareTo(b.getCreation());
		 	}
	 };
	
	 private Comparator<MediaItem> MODIFIED_ORDER =
		 
         new Comparator<MediaItem>() {
		 
		 	@Override
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getModified().compareTo(b.getModified());
		 	}
	 };
	 
	 private Comparator<MediaItem> VERSION_ORDER =
						 
	         new Comparator<MediaItem>() {
			 
		 		@Override
			 	public int compare(MediaItem a, MediaItem b) {
			 		
			 		if(a.getVersion() < b.getVersion()) return(-1);
			 		if(a.getVersion() == b.getVersion()) return(0);
			 		else return(1);
			 	}
	 };
	 
	 private Comparator<MediaItem> MEDIA_ORDER =
			 
	         new Comparator<MediaItem>() {
			 
		 	@Override
		 	public int compare(MediaItem a, MediaItem b) {
		 		
		 		return a.getMimeType().compareTo(b.getMimeType());
		 	}
	 };
	 
	public QueryTableBean() {
		
		mediaList = new ArrayList<MediaTableItem>();
						
		currentSortMode = FILENAME_ORDER;
		
	}
				
	public void sortByFileName() {
				
		if(currentSortMode.equals(FILENAME_ORDER)) {
			
			currentSortMode = Collections.reverseOrder(FILENAME_ORDER);
		
		} else {
		
			currentSortMode = FILENAME_ORDER;
		}
		
		Collections.sort(mediaList, currentSortMode);
	}
	
	public void sortBySizeBytes() {
		
		if(currentSortMode.equals(SIZEBYTES_ORDER)) {
			
			currentSortMode = Collections.reverseOrder(SIZEBYTES_ORDER);
		
		} else {
		
			currentSortMode = SIZEBYTES_ORDER;
		}
		
		Collections.sort(mediaList, currentSortMode);		
	}
	
	public void sortByCreation() {
			
		if(currentSortMode.equals(CREATION_ORDER)) {
			
			currentSortMode = Collections.reverseOrder(CREATION_ORDER);
		
		} else {
		
			currentSortMode = CREATION_ORDER;
		}
		
		Collections.sort(mediaList, currentSortMode);	
	}
	
	public void sortByModified() {
			
		if(currentSortMode.equals(MODIFIED_ORDER)) {
			
			currentSortMode = Collections.reverseOrder(MODIFIED_ORDER);
		
		} else {
		
			currentSortMode = MODIFIED_ORDER;
		}
		
		Collections.sort(mediaList, currentSortMode);	
	}
	
	public void sortByVersion() {
				
		if(currentSortMode.equals(VERSION_ORDER)) {
			
			currentSortMode = Collections.reverseOrder(VERSION_ORDER);
		
		} else {
		
			currentSortMode = VERSION_ORDER;
		}
		
		Collections.sort(mediaList, currentSortMode);	
	}
	
	public void sortByMedia() {
		
		if(currentSortMode.equals(MEDIA_ORDER)) {
			
			currentSortMode = Collections.reverseOrder(MEDIA_ORDER);
		
		} else {
		
			currentSortMode = MEDIA_ORDER;
		}
		
		Collections.sort(mediaList, currentSortMode);	
	}
		
	public void setMediaList(List<MediaItem> media) {
		
		mediaList = new ArrayList<MediaTableItem>();
		
		for(int i = 0; i < media.size(); i++) {
			
			mediaList.add(new MediaTableItem(media.get(i)));
			
		}
		
		Collections.sort(mediaList, currentSortMode);
	}
	
	public DataModel<MediaTableItem> getQueryTableModel() {
		
		queryTable = new ListDataModel<MediaTableItem>(mediaList);
		
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
/*
		UIData table = (UIData)findComponent(context.getViewRoot(), "table");
		
		for(int i = 0; i < table.getRows(); i++) {
			
			table.setRowIndex(i);
			MediaTableItem rowData = (MediaTableItem) table.getRowData();
			
			rowData.setSelected(false);
			
			//if(rowData.isSelected()) {
				
			//int j = 0;
			//}
			
		}				
*/		
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
	
	
	  private UIComponent findComponent(UIComponent c, String id) {
		    if (id.equals(c.getId())) {
		      return c;
		    }
		    Iterator<UIComponent> kids = c.getFacetsAndChildren();
		    while (kids.hasNext()) {
		      UIComponent found = findComponent(kids.next(), id);
		      if (found != null) {
		        return found;
		      }
		    }
		    return null;
		  }
	
}
