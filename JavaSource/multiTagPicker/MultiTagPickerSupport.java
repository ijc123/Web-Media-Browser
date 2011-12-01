package multiTagPicker;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import database.TagTable;

public class MultiTagPickerSupport {

	protected List<String> tags;
	
	protected MultiTagPickerSupport() {
		
		tags = null;		
	}
			
	public void setSelectedTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getSelectedTags() {
					
		if(tags == null) {
			
			tags = new ArrayList<String>();
		}
		
		return(tags);
	}
	
	public void setClearSelectedTags(String dummy){
		
		if(tags == null) return;
		tags.clear();
		
	}
	
	public String getClearSelectedTags() {
		
		return("");
		
	}
			
	public void setAddTag(String name) {
		
		if(name == null) return;
		
		TagTable tagTable = null;
		
		try {
			tagTable = (TagTable) new InitialContext().lookup("java:module/TagTable");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(tagTable.getTagByName(name) == null) return;
		
		if(tags == null) return;
		
		if(tags.contains(name)) return;
		
		tags.add(name);
		
	}
	
	public String getAddTag() {
		
		return("");
	}
	
	public void setRemoveTag(String name) {
		
		if(tags == null) return;
		
		if(tags.contains(name)) {
			
			tags.remove(name);
		}
		
	}
	
	public String getRemoveTag() {
		
		return("");
	}

}
