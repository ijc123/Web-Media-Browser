package database;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import utils.MimeType;


public class MediaItem implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	private String uri;
	private long sizeBytes;
	private String fileName;
	private Timestamp creation;
	private Timestamp modified;
	private int version;
	private ArrayList<String> tagNames;
	private ArrayList<String> typeNames;
			
	public MediaItem() {
						
		uri = "";
		fileName = "";
		sizeBytes = 0;
		
		creation = java.sql.Timestamp.valueOf("0000-00-00 00:00:00");
		modified = java.sql.Timestamp.valueOf("0000-00-00 00:00:00");
		
		tagNames = new ArrayList<String>();
		typeNames = new ArrayList<String>();
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
	public String getMimeType() {
	
		return MimeType.getMimeTypeFromExt(getFileName());

	}	
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}

	public void setTagNames(ArrayList<String> tags) {
		
		this.tagNames = tags;
	
	}

	public ArrayList<String> getTagNames() {
				
		return tagNames;
	}
	
	public List<TagItem> getTagItems() {
		
		ArrayList<String> names = getTagNames();
		
		TagEJB tagEJB = null;
		List<TagItem> tagItems = new ArrayList<TagItem>();
		
		try {
			
			tagEJB = (TagEJB) new InitialContext().lookup("java:module/TagEJB");
			tagItems = tagEJB.getTagByName(names);
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
		
		sortTagsByCategory(tagItems);
		
		return(tagItems);
		
	}

	public String getPath() {
		
		String path = uri.replace("file:/", "");
		
		try {
			
			path = URLDecoder.decode(path, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		return path;
	}

	public void setSizeBytes(long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public Long getSizeBytes() {
		return sizeBytes;
	}

	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}

	public Timestamp getCreation() {
		return creation;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

	public Timestamp getModified() {
		return modified;
	}

	public ArrayList<String> getTypeNames() {
		return typeNames;
	}

	public void setTypeNames(ArrayList<String> typeNames) {
		this.typeNames = typeNames;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == this) return true;
		if (other == null) return false;
		if (getClass() != other.getClass()) return false;

		MediaItem media = (MediaItem)other;

		return (uri.equals(media.getUri()) &&
				sizeBytes == media.getSizeBytes() &&
				fileName.equals(media.getFileName()) &&
				creation.equals(media.getCreation()) &&
				modified.equals(media.getModified()) &&
				version == media.getVersion() &&
				tagNames.equals(media.getTagNames()) &&
				typeNames.equals(media.getTypeNames()));	
	}
	
	@Override
	public Object clone() {
		
		try {
	
			return super.clone();
				
		} catch(CloneNotSupportedException e) {
	
			return null;
		}
	} 

	public boolean isVideo() {
		
		if(getMimeType().startsWith("video")) return(true);
		else return(false);
	}
	
	public boolean isAudio() {
		
		if(getMimeType().startsWith("audio")) return(true);
		else return(false);
	}
	
	public boolean isImage() {
		
		if(getMimeType().startsWith("image")) return(true);
		else return(false);
	}
	
	private Comparator<TagItem> TAG_CATEGORY_ORDER =
			 
	         new Comparator<TagItem>() {
			 
			 	@Override
			 	public int compare(TagItem a, TagItem b) {
			 		
			 		return a.getCategory().get(0).getFullName().compareTo(b.getCategory().get(0).getFullName());
			 	}
		 };
	
	private void sortTagsByCategory(List<TagItem> tagItem) {
		
		Collections.sort(tagItem, TAG_CATEGORY_ORDER);
		
	}
}
