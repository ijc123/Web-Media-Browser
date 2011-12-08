package database;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TagItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int id;
	private int used;
	private Timestamp modified;
	private Timestamp creation;
	private List<String> linkedTagNames;	
	private ArrayList<CategoryItem> category;
	
	private String tagImageURL;
	
	public TagItem() {
		
		this.name = "";
		
		id = 0;
		used = 0;
		modified = java.sql.Timestamp.valueOf("0000-00-00 00:00:00");
		creation =  java.sql.Timestamp.valueOf("0000-00-00 00:00:00");
		
		this.tagImageURL = "";
		linkedTagNames = new ArrayList<String>();
		setCategory(new ArrayList<CategoryItem>());
		
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	public Timestamp getCreation() {
		return creation;
	}
	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}
	
	public List<String> getLinkedTagNames() {
		return linkedTagNames;
	}

	public void setLinkedTagNames(List<String> linkedTagNames) {
		this.linkedTagNames = linkedTagNames;
	}
	public boolean isTagImageAvailable() {
		
		TagEJB tagEJB = null;
		
		try {
			tagEJB = (TagEJB) new InitialContext().lookup("java:module/TagEJB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return(tagEJB.hasTagImage(name));		
	}
		
	public String getFullTagImageURL() {
	
		if(isTagImageAvailable() == false) {
		
			 tagImageURL = "/mijngod/javax.faces.resource/NoPhotoAvailable.jpg.jsf?ln=images";
			
		}
		
		if(!tagImageURL.isEmpty()) return(tagImageURL);
		
		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String actionURL = handler.getActionURL(context, "/tagimage");
				
		tagImageURL = "";
		
		try {
			
			tagImageURL = URLEncoder.encode(name, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		tagImageURL = actionURL + "?id=" + tagImageURL;
		
		return(tagImageURL);
		
	}
	
	public String getShortTagImageURL() {
		
		String url;
		
		if(isTagImageAvailable() == false) {
			
			 url = "/mijngod/javax.faces.resource/NoPhotoAvailable.jpg.jsf?ln=images";
			
			 return(url);
		}
		
		url = getFullTagImageURL();
		
		url = url.substring(1);
		url = url.substring(url.indexOf('/'));
		
		return(url);
	}

	public ArrayList<CategoryItem> getCategory() {
		return category;
	}

	public void setCategory(ArrayList<CategoryItem> category) {
		
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
			
}
