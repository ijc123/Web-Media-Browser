package database;

public class ImageItem {

	private String owner;
	private String mimeType;
	private int sizeBytes;
	
	private byte[] imageData;
	
	public ImageItem() {
		
		owner = "";
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String tag) {
		this.owner = tag;
	}
}
