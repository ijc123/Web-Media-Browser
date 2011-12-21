package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import database.CategoryItem;
import database.CategoryEJB;
import database.TagItem;
import database.TagEJB;
import database.TypeItem;
import database.TypeEJB;

@ViewScoped
@Named
public class CreateTagBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private CategoryEJB categoryEJB;
	@Inject
	private TypeEJB typeEJB;
	@Inject
	private TagEJB tagEJB;
	
	private boolean isNewTag;
	private String selectedTag;
	private DataModel<CategoryItem> dataModel;
	private TagItem tag;
	private String resourceURL;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void Init() {
		
		setNewTag(true);

	}
		
	public TagItem getTag() {
		
		return(tag);
	}
	
	public DataModel<CategoryItem> getDataModel() {
		
		dataModel = new ListDataModel<CategoryItem>(tag.getCategory());
				
		return(dataModel);
	}
	
	public List<CategoryItem> getCategories() {
		
		int index = dataModel.getRowIndex();
		
		List<CategoryItem> categories = new ArrayList<CategoryItem>();
		
		List<CategoryItem> tagCategory = tag.getCategory();
		
		if(!tagCategory.isEmpty()) {
		
			categories = categoryEJB.getCategoriesByType(tagCategory.get(index).getTypeName());
		}
		
		return(categories);
		
	}
	
	public void addTagCategory() {
	
		ArrayList<CategoryItem> items = tag.getCategory();
		
		CategoryItem newCategory = new CategoryItem();
		
		try {
			
			newCategory = (CategoryItem) items.get(0).clone();
			
		} catch (CloneNotSupportedException e) {
		
			e.printStackTrace();
		}
		
		items.add(newCategory);
		
		tag.setCategory(items);

	}
	
	public void deleteTagCategory() {
		
		int index = dataModel.getRowIndex();
		
		ArrayList<CategoryItem> items = tag.getCategory();
		
		items.remove(index);
		
		tag.setCategory(items);
	}

	public void setResourceURL(String resourceURL) {
		
		this.resourceURL = resourceURL;
	}

	public String getResourceURL() {

		return(resourceURL);

	}

	public void modify() {
	
		if(tag.getName().isEmpty()) return;
		
		boolean success = tagEJB.modifyTag(tag, resourceURL);
		
		if(success) {

			setNewTag(false);
			selectedTag = tag.getName();
		}
	}

	public void deleteTag() {

		tagEJB.deleteTag(tag);

		setNewTag(true);		
	}

	public String getSelectedTag() {
		
		return selectedTag;
	}

	public void setSelectedTag(String selectedTag) {
		
		TagItem temp = tagEJB.getTagByName(selectedTag);
		
		if(temp != null && !selectedTag.equals(this.selectedTag)) {
			
			setNewTag(false);
			tag = temp;
			
			this.selectedTag = selectedTag;
		}
		
	}

	public boolean isNewTag() {
		return isNewTag;
	}

	public void setNewTag(boolean isNewTag) {
		
		if(this.isNewTag != isNewTag && isNewTag == true) {
					
			selectedTag = "";
			tag = newEmptyTag();		
		}
		
		resourceURL= "";
		this.isNewTag = isNewTag;
		
	}
	
	private TagItem newEmptyTag() {
		
		TagItem newTag = new TagItem();
		
		CategoryItem dummyCategory = new CategoryItem();
		
		List<TypeItem> types = typeEJB.getAllTypes();
		
		if(!types.isEmpty()) {
					
			dummyCategory.setTypeName(types.get(0).getName());
		}
		
		ArrayList<CategoryItem> categories = new ArrayList<CategoryItem>();
		
		categories.add(dummyCategory);
		
		newTag.setCategory(categories);
		
		return(newTag);
		
	}



}


