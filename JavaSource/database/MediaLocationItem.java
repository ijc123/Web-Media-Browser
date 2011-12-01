package database;

public class MediaLocationItem {
	
	private String location;
	private String typeName;
	private boolean video;
	private boolean audio;
	private boolean images;
	
	public MediaLocationItem() {
		
		location = "";
		typeName = "";
		video = false;
		audio = false;
		images = false;
		
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public boolean isVideo() {
		return video;
	}
	public void setVideo(boolean video) {
		this.video = video;
	}
	public boolean isAudio() {
		return audio;
	}
	public void setAudio(boolean audio) {
		this.audio = audio;
	}
	public boolean isImages() {
		return images;
	}
	public void setImages(boolean images) {
		this.images = images;
	}
	
}