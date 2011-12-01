package database;

public class TagImageItem {

	private String tag;
	private String mimeType;
	private int sizeBytes;
	
	private byte[] imageData;
	
	public TagImageItem() {
		
		tag = "";
		mimeType = "";
		sizeBytes = 0;
		imageData = null;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public int getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(int sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] data) {
		this.imageData = data;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
