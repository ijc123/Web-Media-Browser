package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Min;

import beans.queryTable.QueryTableBean;
import database.MediaEJB;
import database.MediaItem;

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
	
	@Min(value=0, message="Min Edits should be a positive integer")
	private Integer minVersion;
	@Min(value=0, message="Max Edits should be a positive integer")
	private Integer maxVersion;
	
	private List<String> excludeTypes;
	
	public AddTagBean() {
		
		super();
		
		setLocale(Locale.US);
				
		minVersion = 0;
		maxVersion = 1024;
		
		fileNameQuery = "";
		
		excludeTypes = new ArrayList<String>();
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
					toTimestamp, minVersion, maxVersion, excludeTypes);

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

	public int getMinVersion() {
		
		return(minVersion);
	}

	public void setMinVersion(int minVersion) {
		
		this.minVersion = minVersion;				
	}

	public int getMaxVersion() {
		
		return(maxVersion);
	}

	public void setMaxVersion(int maxVersion) {
		
		this.maxVersion = maxVersion;	
	}

	public List<String> getExcludeTypes() {		
		return excludeTypes;
	}

	public void setExcludeTypes(List<String> excludeTypes) {
		this.excludeTypes = excludeTypes;
	}

	
		
}

