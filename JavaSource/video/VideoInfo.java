package video;

public class VideoInfo {
	
	private String filename;
	private int nrStreams;
	private String formatName;
	private String formatLongName;
	private double startTimeSeconds;
	private double durationSeconds;
	private int sizeBytes;
	private int bitRate;
	private String encoderTag;
	
	private int width;
	private int height;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getNrStreams() {
		return nrStreams;
	}
	public void setNrStreams(int nrStreams) {
		this.nrStreams = nrStreams;
	}
	public String getFormatName() {
		return formatName;
	}
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}
	public String getFormatLongName() {
		return formatLongName;
	}
	public void setFormatLongName(String formatLongName) {
		this.formatLongName = formatLongName;
	}
	public double getStartTimeSeconds() {
		return startTimeSeconds;
	}
	public void setStartTimeSeconds(double startTimeSeconds) {
		this.startTimeSeconds = startTimeSeconds;
	}
	public double getDurationSeconds() {
		return durationSeconds;
	}
	public void setDurationSeconds(double durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
	public int getSizeBytes() {
		return sizeBytes;
	}
	public void setSizeBytes(int sizeBytes) {
		this.sizeBytes = sizeBytes;
	}
	public int getBitRate() {
		return bitRate;
	}
	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}
	public String getEncoderTag() {
		return encoderTag;
	}
	public void setEncoderTag(String encoderTag) {
		this.encoderTag = encoderTag;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

}
