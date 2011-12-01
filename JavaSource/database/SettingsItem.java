package database;

import java.sql.Timestamp;
import java.util.List;

public class SettingsItem {
	
	private int nrMediaItems;
	private int nrMediaItemsAdded;
	private int nrMediaItemsRemoved;
	
	private String previewRootDirectory;	
	private List<MediaLocationItem> mediaLocations;		
	private Timestamp lastSynchronized;
				
	public void setPreviewRootDirectory(String previewRootDirectory) {
		this.previewRootDirectory = previewRootDirectory;
	}

	public String getPreviewRootDirectory() {
		return previewRootDirectory;
	}

	public void setSynchronized(Timestamp lastSynchronized) {
		this.lastSynchronized = lastSynchronized;
	}

	public Timestamp getSynchronized() {
		return lastSynchronized;
	}

	public List<MediaLocationItem> getMediaLocations() {
		return mediaLocations;
	}

	public void setMediaLocations(List<MediaLocationItem> mediaLocation) {
		this.mediaLocations = mediaLocation;
	}

	public int getNrMediaItems() {
		return nrMediaItems;
	}

	public void setNrMediaItems(int nrMediaItems) {
		this.nrMediaItems = nrMediaItems;
	}

	public int getNrMediaItemsAdded() {
		return nrMediaItemsAdded;
	}

	public void setNrMediaItemsAdded(int nrMediaItemsAdded) {
		this.nrMediaItemsAdded = nrMediaItemsAdded;
	}

	public int getNrMediaItemsRemoved() {
		return nrMediaItemsRemoved;
	}

	public void setNrMediaItemsRemoved(int nrMediaItemsRemoved) {
		this.nrMediaItemsRemoved = nrMediaItemsRemoved;
	}
	
	
}
