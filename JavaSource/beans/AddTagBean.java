package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import beans.queryTable.QueryTableBean;

import database.MediaItem;
import database.MediaEJB;

@ViewScoped
@Named
public class AddTagBean implements Serializable {

	@Inject
	private MediaEJB mediaEJB;
	
	@Inject
	private QueryTableBean queryTable;

	private static final long serialVersionUID = 1L;
	private List<String> selectedTags;
	
	private String fileNameQuery;
	private Date fromDate;
	private Date toDate;
	private Locale locale;
	
	private Integer minVersion;
	private Integer maxVersion;
	
	public AddTagBean() {
		
		super();
		
		setLocale(Locale.US);
				
		minVersion = maxVersion = null;
		
		fileNameQuery = "";
	}
		
	public void doQuery() {
			
		List<MediaItem> mediaList = new ArrayList<MediaItem>();

		if(!(fileNameQuery.isEmpty() && fromDate == null && toDate == null 
				&& minVersion == null && maxVersion == null)) 
		{

			java.sql.Timestamp fromTimestamp = null;

			if(fromDate != null) {

				fromTimestamp = new java.sql.Timestamp(fromDate.getTime());

			}

			java.sql.Timestamp toTimestamp = null;

			if(toDate != null) {

				toTimestamp = new java.sql.Timestamp(toDate.getTime());

			}

			mediaList = mediaEJB.getMediaByFilenameQuery(fileNameQuery, fromTimestamp,
					toTimestamp, minVersion, maxVersion);

		}

		queryTable.setMediaList(mediaList);
		
	}
	
	public void setFileNameQuery(String fileNameQuery) {
			
		this.fileNameQuery = fileNameQuery;		
	
	}
	
	public String getFileNameQuery() {
		
		return fileNameQuery;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getMinVersion() {
		
		if(minVersion == null) return("");
		else return(Integer.toString(minVersion));
	}

	public void setMinVersion(String minVersion) {
		
		if(minVersion.isEmpty()) {
			
			this.minVersion = null;
			
		} else {
			
			this.minVersion = Integer.parseInt(minVersion);
		}			
	}

	public String getMaxVersion() {
		
		if(maxVersion == null) return("");
		else return(Integer.toString(maxVersion));
	}

	public void setMaxVersion(String maxVersion) {
		
		if(maxVersion.isEmpty()) {
			
			this.maxVersion = null;
			
		} else {
			
			this.maxVersion = Integer.parseInt(maxVersion);
		}	

	}

	
		
}

