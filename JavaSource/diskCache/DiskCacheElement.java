package diskCache;

import java.io.Serializable;

public class DiskCacheElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private long start;
	private long end;
	
	private byte[] buffer;
	
	public DiskCacheElement(String id, long start, long end) {
		
		this.id = id;
		this.start = start;
		this.end = end;
		
		int size = (int)(end - start);
		
		buffer = new byte[size];
		
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public String getId() {
		return id;
	}

	
}
