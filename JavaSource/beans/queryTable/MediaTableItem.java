package beans.queryTable;

import database.MediaItem;

public class MediaTableItem extends MediaItem {

	private boolean selected;
	private String selectedTab;
	
	// creates a reference copy, not a cloned copy
	public MediaTableItem(MediaItem m) {
		
		//MediaItem m = (MediaItem)media.clone();
		
		setUri(m.getUri());
		setSizeBytes(m.getSizeBytes());
		setFileName(m.getFileName());
		setCreation(m.getCreation());
		setModified(m.getModified());
		setVersion(m.getVersion());
		setTagNames(m.getTagNames());
		setTypeNames(m.getTypeNames());
		
		selected = false;
		selectedTab = "tags";	
	}
	
	public void setMediaItemData(MediaItem m) {
	
		setUri(m.getUri());
		setSizeBytes(m.getSizeBytes());
		setFileName(m.getFileName());
		setCreation(m.getCreation());
		setModified(m.getModified());
		setVersion(m.getVersion());
		setTagNames(m.getTagNames());
		setTypeNames(m.getTypeNames());
				
	}
	
	public void setSelected(boolean selected) {
		
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
		
	
}
