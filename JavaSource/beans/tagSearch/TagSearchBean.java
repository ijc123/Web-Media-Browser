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

import beans.queryTable.QueryTableBean;

import database.CategoryItem;
import database.CategoryEJB;
import database.MediaItem;
import database.MediaEJB;
import database.TagItem;
import database.TagEJB;
import database.TypeItem;
import database.TypeEJB;

@ViewScoped
@Named
public class TagSearchBean implements Serializable {
		
	private static final long serialVersionUID = 1L;

	private Comparator<TagItem> ALPHABETHICAL_ORDER = new Comparator<TagItem>() {

		@Override
		public int compare(TagItem a, TagItem b) {

			return a.getName().compareTo(b.getName());
		}
	};

	private Comparator<TagItem> USED_ORDER = new Comparator<TagItem>() {

		@Override
		public int compare(TagItem a, TagItem b) {

			if(a.getUsed() > b.getUsed()) return(-1);
			if(a.getUsed() == b.getUsed()) return(0);
			else return(1);

		}
	};

	@Inject
	CategoryEJB categoryEJB;
	@Inject
	TypeEJB typeEJB;
	@Inject
	private TagEJB tagEJB;
	@Inject
	private MediaEJB mediaEJB;
	@Inject
	private QueryTableBean queryTable;
		
	private TypeItem type;
	private CategoryItem category;
	private List<TagItem> tagList;
	
	private List<String> queryTagNames;
	private List<TagItem> queryTagItems;
	
	private List<MediaItem> mediaList;
	private Map<CategoryItem, List<TagItem>> tagMap;
	private boolean updateTagMap;
	
	private int searchMode;
	private boolean isShowAllCategoriesSelected;
	
	private int sortId;
	private Comparator<TagItem> sortMode;
	
	private String selectedTab;
	
	public TagSearchBean() {
						
		updateTagMap = true;
		queryTagNames = new ArrayList<String>();	
		new ArrayList<String>();	
		selectedTab = "category";
		
		mediaList = new ArrayList<MediaItem>();
		
		type = new TypeItem();
		category = new CategoryItem();
		
		setSortMode(0);
		setSearchMode(0);
		
		isShowAllCategoriesSelected = false;
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		
		// initialize default type and category
		List<TypeItem> allTypes = typeEJB.getAllTypes();
		
		if(!allTypes.isEmpty()) {
					
			type = allTypes.get(0);
			
			List<CategoryItem> allCategories = categoryEJB.getCategoriesByType(type.getName());
			
			if(!allCategories.isEmpty()) {
				
				category = allCategories.get(0);
						
			}
			
		} 	
		
	}
	
	private void parentTagSearch() {
		
		List<TagItem> children = new ArrayList<TagItem>();
		
		Set<String> uniqueChildren = new HashSet<String>();
		
		if(queryTagNames.isEmpty()) {
			
			children = tagEJB.getAllTags();
						
		} else {
			
			List<TagItem> parents = tagEJB.getParentTags(queryTagNames);
			
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
			
			children = tagEJB.getTagByName(uniqueChildrenList);					
			
		}
		
		List<String> selectedChildren = new ArrayList<String>(queryTagNames);
						
		for(int i = 0; i < children.size(); i++) {
		
			TagItem child = children.get(i);
			
			selectedChildren.add(child.getName());
			
			List<TagItem> parents = tagEJB.getParentTags(selectedChildren);
						
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
				
				List<TagItem> tags = tagEJB.getTagsByCategory(category.get(i));
				
				tagMap.get(category.get(i)).addAll(tags);
				
			}
			
			return;
		}
		
		
		mediaList = mediaEJB.getMediaByTagQuery(queryTagNames);
		
		Map<String, Integer> uniqueTagsMap = new HashMap<String, Integer>();
		
		for(int i = 0; i < mediaList.size(); i++) {
			
			ArrayList<String> mediaTags = mediaList.get(i).getTagNames();
			
			for(int j = 0; j < mediaTags.size(); j++) {
				
				String mediaTag = mediaTags.get(j);
				
				if(queryTagNames.contains(mediaTag)) {
				
					continue;
					
				} if(uniqueTagsMap.get(mediaTag) == null) {
					
					uniqueTagsMap.put(mediaTag, 1);
				
				} else {
					
					int count = uniqueTagsMap.get(mediaTag);
					uniqueTagsMap.put(mediaTag, ++count);
					
				}
				
			}
			
		}
		
		List<String> uniqueTagsList = new ArrayList<String>();
				
		Iterator<Map.Entry<String, Integer>> it = uniqueTagsMap.entrySet().iterator();
	   
	    while(it.hasNext()) {
	    	
	    	Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
	    	
	    	uniqueTagsList.add((String)pairs.getKey());
	    	   	 
	    }

	    List<TagItem> result = tagEJB.getTagByName(uniqueTagsList);
	    
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
		
		List<CategoryItem> category = categoryEJB.getCategoriesByType(type.getName());
			
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
		
		List<CategoryItem> category = categoryEJB.getCategoriesByType(type.getName());
		
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
		
		if(!isShowAllCategoriesSelected) {
			
			// only get the tags for the selected category
			tagList = tagMap.get(category);
			
		} else {

			tagList.clear();
			
			// concat all tags for all categories
			for(List<TagItem> tags : tagMap.values()) {
			    
				tagList.addAll(tags);
			}
			
		}
					
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
				
		queryTagItems =  tagEJB.getTagByName(queryTagNames);
		
	}

	public List<String> getHiddenQueryTags() {
				
		return queryTagNames;
	}

	public void queryTableSearch() {

		List<MediaItem> queryResults = mediaEJB.getMediaByTagQuery(queryTagNames);

		queryTable.setMediaList(queryResults);
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
			
			List<CategoryItem> allCategories = categoryEJB.getCategoriesByType(type.getName());
			
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

	public boolean isShowAllCategoriesSelected() {
		return isShowAllCategoriesSelected;
	}

	public void setShowAllCategoriesSelected(boolean isShowAllCategoriesSelected) {
		
		if(isShowAllCategoriesSelected != this.isShowAllCategoriesSelected) {
					
			updateTagMap = true;		
		}
		
		this.isShowAllCategoriesSelected = isShowAllCategoriesSelected;
	}


	
}
