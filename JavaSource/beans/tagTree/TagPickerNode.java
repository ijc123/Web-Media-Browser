package beans.tagTree;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import database.TagItem;
import database.TagEJB;

public class TagPickerNode extends Node<TagPickerNode> {

	List<String> tags;


	public TagPickerNode(String path) {
		
		super(path, TagPickerNode.class);
        
	}
		
	public List<String> getTags() {
		
		if(tags != null) return(tags);
						
		TagEJB tagEJB = null;
		
		try {
			tagEJB = (TagEJB) new InitialContext().lookup("java:module/TagEJB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<TagItem> result = tagEJB.getTagsByCategory(category);
		
		tags = new ArrayList<String>();
		
		for(int i = 0; i < result.size(); i++) {
			
			tags.add(result.get(i).getName());
			
		}
		
		return(tags);
	}
	
}
