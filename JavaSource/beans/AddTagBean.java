package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.MediaItem;
import database.MediaEJB;

@ViewScoped
@Named
public class AddTagBean implements Serializable {

	@Inject
	private MediaEJB mediaEJB;

	private static final long serialVersionUID = 1L;
	private List<String> selectedTags;
	private List<MediaItem> mediaList;
	
	private String fileNameQuery;
	private Date selectedDate;
	private Locale locale;
	
	public AddTagBean() {
		
		super();
		
		setLocale(Locale.US);
		
		mediaList = new ArrayList<MediaItem>();
	}
	
	public void setMediaList(List<MediaItem> mediaList) {
		
	}
	
	public List<MediaItem> getMediaList() {
			
		if(fileNameQuery == null && selectedDate == null) {
						
			return(new ArrayList<MediaItem>());		
		}
		
		java.sql.Timestamp sqlTimestamp = null;
		
		if(selectedDate != null) {
			
			sqlTimestamp = new java.sql.Timestamp(selectedDate.getTime());
			
		}
		
		mediaList = mediaEJB.getMediaByFilenameQuery(fileNameQuery, sqlTimestamp);
		
		return(mediaList);
	}
	
	public void setFileNameQuery(String fileNameQuery) {
			
		this.fileNameQuery = fileNameQuery;		
	
	}
	
	public String getFileNameQuery() {
		
		return fileNameQuery;
	}
	
	public void setSelectedDate(Date selectedDate) {
		
		this.selectedDate = selectedDate;
		
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setPattern(String pattern) {

	}

	public String getPattern() {
		return "yyyy-MM-d HH:mm";
	}

	
	public void setSelectedTags(List<String> tags) {
		this.selectedTags = tags;
	}

	public List<String> getSelectedTags() {
		
		if(selectedTags == null) {
			
			selectedTags = new ArrayList<String>();
		}
		
		return selectedTags;
	}

	
		
}

