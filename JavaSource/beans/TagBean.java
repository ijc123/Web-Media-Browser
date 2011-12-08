package beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.TagItem;
import database.TagEJB;

@RequestScoped
@Named
public class TagBean {

	@Inject
	private TagEJB tagEJB;
	
	static final Comparator<TagItem> USAGE_ORDER = 
			
		new Comparator<TagItem>() {
	
			public int compare(TagItem a, TagItem b) {
	
				if(a.getUsed() < b.getUsed()) return(1);
				if(a.getUsed() == b.getUsed()) return(0);
				else return(-1);
			}
		};
		
	static final Comparator<TagItem> ALPHABETICAL_ORDER = 
			
			new Comparator<TagItem>() {
		
				public int compare(TagItem a, TagItem b) {
		
					return(a.getName().compareTo(b.getName()));
				}
			};

	public List<String> suggestAutoComplete(String input) {

		ArrayList<String> names= new ArrayList<String>();

		List<TagItem> tags = tagEJB.getTagByNameQuery(input);

		Collections.sort(tags, USAGE_ORDER);

		for (int i = 0; i < tags.size(); i++) {

			//  ret.add(new Tag(result.getData(i, 0), result.getData(i, 1)));
			names.add(tags.get(i).getName());
		}

		return names;
	}

	public List<String> getAllTags() {

		ArrayList<String> names = new ArrayList<String>();

		List<TagItem> tags = tagEJB.getAllTags();

		Collections.sort(tags, ALPHABETICAL_ORDER);
		
		for (int i = 0; i < tags.size(); i++) {

			names.add(tags.get(i).getName());
		}

		return names;
	}
	
}
