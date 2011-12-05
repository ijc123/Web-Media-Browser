package beans.tagSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import database.CategoryItem;
import database.CategoryTable;
import database.MediaItem;
import database.MediaTable;
import database.TagItem;
import database.TagTable;
import database.TypeItem;
import database.TypeTable;

@ViewScoped
@Named
public class TagSearchBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final Comparator<TagItem> ALPHABETHICAL_ORDER = new Comparator<TagItem>() {

		public int compare(TagItem a, TagItem b) {

			return a.getName().compareTo(b.getName());
		}
	};

	static final Comparator<TagItem> USED_ORDER = new Comparator<TagItem>() {

		public int compare(TagItem a, TagItem b) {

			if(a.getUsed() > b.getUsed()) return(-1);
			if(a.getUsed() == b.getUsed()) return(0);
			else return(1);

		}
	};

	@Inject
	CategoryTable categoryTable;
	@Inject
	TypeTable typeTable;
	@Inject
	private TagTable tagTable;
	@Inject
	private MediaTable mediaTable;
	
	private TypeItem type;
	private CategoryItem category;
	private List<TagItem> tagList;
	
	private List<String> queryTagNames;
	private List<TagItem> queryTagItems;
	
	private List<String> queryTableSearchTags;
	private List<MediaItem> mediaList;
	private Map<CategoryItem, List<TagItem>> tagMap;
	private boolean updateTagMap;
	
	private int searchMode;
	
	private int sortId;
	private Comparator<TagItem> sortMode;
	
	private String selectedTab;
	
	public TagSearchBean() {
				
		updateTagMap = true;
		queryTagNames = new ArrayList<String>();	
		queryTableSearchTags = new ArrayList<String>();	
		selectedTab = "category";
		
		type = new TypeItem();
		category = new CategoryItem();
		
		setSortMode(0);
		setSearchMode(0);
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		
		// initialize default type and category
		List<TypeItem> allTypes = typeTable.getAllTypes();
		
		if(!allTypes.isEmpty()) {
					
			type = allTypes.get(3);
			
			List<CategoryItem> allCategories = categoryTable.getCategoriesByType(type.getName());
			
			if(!allCategories.isEmpty()) {
				
				category = allCategories.get(0);
						
			}
			
		} 	
		
	}
	
	private void parentTagSearch() {
		
		List<TagItem> children = new ArrayList<TagItem>();
		
		Set<String> uniqueChildren = new HashSet<String>();
		
		if(queryTagNames.isEmpty()) {
			
			children = tagTable.getAllTags();
						
		} else {
			
			List<TagItem> parents = tagTable.getParentTags(queryTagNames);
			
			for(int j = 0; j < parents.size(); j++) {
		    	
	    		TagItem parent = parents.get(j);
	    		
	    		List<String> linked = parent.getLinkedTagNames();
	    	
	    		uniqueChildren.add(parent.getName());
	    		uniqueChildren.addAll(linked);	  
	    		
	    	}
			
			uniqueChildren.removeAll(queryTagNames);
			
			Object[] temp1 = uniqueChildren.toArray();
			
			String[] temp2 = Arrays.copyOf(temp1, temp1.length, String[].class);
			
			List<String> uniqueChildrenList = new ArrayList<String>(Arrays.asList(temp2));
			
			children = tagTable.getTagByName(uniqueChildrenList);					
			
		}
		
		List<String> selectedChildren = new ArrayList<String>(queryTagNames);
						
		for(int i = 0; i < children.size(); i++) {
		
			TagItem child = children.get(i);
			
			selectedChildren.add(child.getName());
			
			List<TagItem> parents = tagTable.getParentTags(selectedChildren);
						
			child.setUsed(parents.size());
			
			uniqueChildren.add(child.getName());
			
			selectedChildren.remove(child.getName());
			
			// insert into tagMap
	    	List<CategoryItem> tagCategory = child.getCategory();
	    	
	    	for(int j = 0; j < tagCategory.size(); j++) {
	    		
	    		if(tagCategory.get(j).getTypeName().equals(type.getName())) {
	    			
	    			tagMap.get(tagCategory.get(j)).add(child);
	    		}
	    	}				    
		}
						
	}
	
	private void mediaTagSearch(List<CategoryItem> category) {
		
		if(queryTagNames.isEmpty()) {
			
			for(int i = 0; i < category.size(); i++) {
				
				List<TagItem> tags = tagTable.getTagsByCategory(category.get(i));
				
				tagMap.get(category.get(i)).addAll(tags);
				
			}
			
			return;
		}
		
		
		List<MediaItem> mediaList = mediaTable.getMediaByTagQuery(queryTagNames);
		
		Map<String, Integer> uniqueTagsMap = new HashMap<String, Integer>();
		
		for(int i = 0; i < mediaList.size(); i++) {
			
			ArrayList<String> mediaTags = mediaList.get(i).getTagNames();
			
			for(int j = 0; j < mediaTags.size(); j++) {
				
				if(uniqueTagsMap.get(mediaTags.get(j)) == null) {
					
					uniqueTagsMap.put(mediaTags.get(j), 1);
				
				} else {
					
					int count = uniqueTagsMap.get(mediaTags.get(j));
					uniqueTagsMap.put(mediaTags.get(j), ++count);
					
				}
				
			}
			
		}
		
		List<String> uniqueTagsList = new ArrayList<String>();
				
		Iterator<Map.Entry<String, Integer>> it = uniqueTagsMap.entrySet().iterator();
	   
	    while(it.hasNext()) {
	    	
	    	Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
	    	
	    	uniqueTagsList.add((String)pairs.getKey());
	    	   	 
	    }

	    List<TagItem> result = tagTable.getTagByName(uniqueTagsList);
	    
	    for(int i = 0; i < result.size(); i++) {
	    	
	    	TagItem tag = result.get(i);
	    	
	    	tag.setUsed( uniqueTagsMap.get(tag.getName()) );
	    	
	    	// insert into tagMap
	    	List<CategoryItem> tagCategory = tag.getCategory();
	    	
	    	for(int j = 0; j < tagCategory.size(); j++) {
	    		
	    		if(tagCategory.get(j).getTypeName().equals(type.getName())) {
	    			
	    			tagMap.get(tagCategory.get(j)).add(tag);
	    		}
	    	}
	    	
	    }
	    
				
	}
	
	private void populateTagMap() {
			
		// add categories to tagMap
		tagMap = new TreeMap<CategoryItem, List<TagItem>>();
		
		List<CategoryItem> category = categoryTable.getCategoriesByType(type.getName());
			
		for(int i = 0; i < category.size(); i++) {
			
			List<TagItem> tagsInCategoryList = new ArrayList<TagItem>();
			
			tagMap.put(category.get(i), tagsInCategoryList);
						
		}
		
		if(searchMode == 0) {
					
			mediaTagSearch(category);
		
		} else { 
			
			parentTagSearch();
		}
								
	}
	
	
	public List<String> getCategoryList() {
		
		List<CategoryItem> category = categoryTable.getCategoriesByType(type.getName());
		
		List<String> categoryName = new ArrayList<String>();
		
		for(int i = 0; i < category.size(); i++) {
			
			categoryName.add(category.get(i).getName());
		}
		
		return(categoryName); 
	}

	
	public void setCategory(String category) {
		
		this.category.setName(category);
		
	}
	
	public String getCategory() {
	
		return category.getName();
	}
	
	public List<TagItem> getRelevantTagsByCategoryList() {
		
		if(updateTagMap == true) {
			
			populateTagMap();
			updateTagMap = false;
		}
		
		tagList = tagMap.get(category);
				
		Collections.sort(tagList, sortMode);
			
		return tagList;
	}
		
	public void setHiddenQueryTags(List<String> queryTags) {
		
		this.queryTagNames = queryTags;
		updateTagMap = true;
		
	    Map<String, String> reqParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	    
	    if(reqParams.get("selectedTab") != null) {
	    
	    	selectedTab = reqParams.get("selectedTab");
	    }
				
		queryTagItems =  tagTable.getTagByName(queryTagNames);
		
	}

	public List<String> getHiddenQueryTags() {
				
		return queryTagNames;
	}

	public void queryTableSearch() {
		
		queryTableSearchTags.clear();
		
		for(int i = 0; i < queryTagNames.size(); i++) {
	
			queryTableSearchTags.add(queryTagNames.get(i));
		}
	
		
	}
	
	public List<MediaItem> getMediaList() {
		
		mediaList = new ArrayList<MediaItem>();
		
		if(queryTableSearchTags.isEmpty()) return(mediaList);
		
		mediaList = mediaTable.getMediaByTagQuery(queryTableSearchTags);
		
		return mediaList;
	}

	public void setQueryTagItems(List<TagItem> queryTagItems) {
		this.queryTagItems = queryTagItems;
	}

	public List<TagItem> getQueryTagItems() {
		return queryTagItems;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	
	public void setTypeName(String typeName) {
		
		if(!typeName.equals(type.getName())) {
			
			type.setName(typeName);
			
			List<CategoryItem> allCategories = categoryTable.getCategoriesByType(type.getName());
			
			if(!allCategories.isEmpty()) {
				
				category = allCategories.get(0);						
			}
			
			updateTagMap = true;
		}
		
	}

	public String getTypeName() {
		
		return type.getName();
	}

	public int getSortMode() {
		return sortId;
	}

	public void setSortMode(int sortId) {
		
		this.sortId = sortId;
		
		if(sortId == 0) {
			
			sortMode = ALPHABETHICAL_ORDER;
			
		} else {
			
			sortMode = USED_ORDER;
		}
		
		updateTagMap = true;
	}

	public int getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(int searchMode) {
		this.searchMode = searchMode;
		updateTagMap = true;
	}


	
}
