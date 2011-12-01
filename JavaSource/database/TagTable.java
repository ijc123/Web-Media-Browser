package database;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.jboss.ejb3.annotation.IgnoreDependency;

@Stateless
public class TagTable {

	@EJB
	@IgnoreDependency
	private MediaTable mediaTable;
	
	public TagItem getTagByName(String name) {
		
		if(name == null) return(null);
		
		ArrayList<String> temp = new ArrayList<String>();
		
		temp.add(name);
		
		TagItem tag = null;
		
		List<TagItem> result = getTagByName(temp);
		
		if(!result.isEmpty()) {
			
			tag = result.get(0);
		}
		
		return(tag);		
	}

	@SuppressWarnings("unchecked")
	public List<TagItem> getTagByName(List<String> name) {
						
		List<TagItem> results = new ArrayList<TagItem>();
				
		if(name.isEmpty()) return(results);
						
		SqlSession session = MyBatis.getSession().openSession(); 
		
		results = (List<TagItem>) session.selectList("database.TagMapper.getTagsByName", name);
			
		session.close();
		
		return(results);		
	}
	
	private TagItem getTagById(int id) {
				
		ArrayList<Integer> temp = new ArrayList<Integer>();
		
		temp.add(id);
		
		TagItem tag = null;
		
		List<TagItem> result = getTagById(temp);
		
		if(!result.isEmpty()) {
			
			tag = result.get(0);
		}
		
		return(tag);		
	}
	
	@SuppressWarnings("unchecked")
	private List<TagItem> getTagById(List<Integer> id) {
						
		List<TagItem> results = new ArrayList<TagItem>();
				
		if(id.isEmpty()) return(results);
						
		SqlSession session = MyBatis.getSession().openSession(); 
		
		results = (List<TagItem>) session.selectList("database.TagMapper.getTagsById", id);
			
		session.close();
		
		return(results);		
	}
	
	@SuppressWarnings("unchecked")
	public List<TagItem> getTagsByCategory(CategoryItem category) {
			
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<TagItem> tag = (List<TagItem>) session.selectList("database.TagMapper.getTagsByCategory", category);
		
		session.close();
		
		return(tag);		
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TagItem> getAllTags() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<TagItem> tag = (List<TagItem>) session.selectList("database.TagMapper.getAllTags");
		
		session.close();
		
		return(tag);		
		
	}
			
	public List<TagItem> addLinkedTags(final List<TagItem> tagList) {
		
		ArrayList<String> tagPool = new ArrayList<String>();
				
		for(int i = 0; i < tagList.size(); i++) {
			
			TagItem tag = tagList.get(i);
			
			if(!tagPool.contains(tag.getName())) {
				
				tagPool.add(tag.getName());
			}
			
			for(int j = 0; j < tag.getLinkedTagNames().size(); j++) {
				
				if(!tagPool.contains(tag.getLinkedTagNames().get(j))) {
					
					tagPool.add(tag.getLinkedTagNames().get(j));
				}				
			}		
		}
		
		List<TagItem> linkedTagsPool = getTagByName(tagPool);
		
		return(linkedTagsPool);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<TagItem> getTagByNameQuery(String name) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<TagItem> tag = (List<TagItem>) session.selectList("database.TagMapper.getTagsByNameQuery", name + "%");
			
		session.close();
		
		return(tag);		
	}
		
	private boolean updateTag(final TagItem newTagItem, final TagItem oldTagItem) {
							
		List<String> oldLinkedTags = oldTagItem.getLinkedTagNames();
		List<String> newLinkedTags = newTagItem.getLinkedTagNames();
						
		oldLinkedTags.removeAll(newLinkedTags);
			
		boolean linkedTagsModified = false;
		
		if(oldLinkedTags.size() != newLinkedTags.size()) {
			
			linkedTagsModified = true;
			
		} else if(!oldLinkedTags.isEmpty()) {
		
			linkedTagsModified = true;
		
		}
		
		deleteTagLinked(newTagItem.getName(), oldLinkedTags);
					
		List<CategoryItem> oldCategories = oldTagItem.getCategory();
		List<CategoryItem> newCategories = newTagItem.getCategory();
		
		oldCategories.removeAll(newCategories);
		
		deleteTagCategory(newTagItem.getName(), oldCategories);
					
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.insert("database.TagMapper.updateTag", newTagItem);								
	
		session.close();
		
		setTagLinked(newTagItem);
		setTagCategory(newTagItem);
	
		return(linkedTagsModified);
	}
	
	private void insertTag(final TagItem newTagItem) {
							
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.insert("database.TagMapper.insertTag", newTagItem);								
	
		session.close();
		
		setTagLinked(newTagItem);
		setTagCategory(newTagItem);
	
	}
	
	private void setTagLinked(final TagItem tagItem) {
		
		List<String> linkedTags = tagItem.getLinkedTagNames();
		
		if(linkedTags == null || linkedTags.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
									
		for(int i = 0; i < linkedTags.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("tag", tagItem.getName());
			map.put("linked", linkedTags.get(i));
		
			session.insert("database.TagMapper.setTagLinked", map);
			
		}
			
		session.close();
	}

	private void deleteTagLinked(String tag, final List<String> linkedTags) {
		
		if(linkedTags.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
					
		for(int i = 0; i < linkedTags.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("tag", tag);
			map.put("linked", linkedTags.get(i));
		
			session.delete("database.TagMapper.deleteTagLinked", map);
			
		}

		session.close();
	}
		
	private void setTagCategory(final TagItem tagItem) {
		
		List<CategoryItem> category = tagItem.getCategory();
		if(category == null || category.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(ExecutorType.BATCH); 
							
		for(int i = 0; i < category.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("tag", tagItem.getName());
			map.put("category", category.get(i).getName());
			map.put("type", category.get(i).getTypeName());
		
			session.insert("database.TagMapper.setTagCategory", map);
			
		}
				
		session.close();
	}
	
	private void deleteTagCategory(String name, final List<CategoryItem> category) {
	
		if(category.isEmpty()) return;
				
		SqlSession session = MyBatis.getSession().openSession(); 
					
		for(int i = 0; i < category.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("tag", name);
			map.put("category", category.get(i).getName());
			map.put("type", category.get(i).getTypeName());
		
			session.delete("database.TagMapper.deleteTagCategory", map);
			
		}				

		session.close();
	}
	
	public List<TagItem> getParentTags(String child) {
		
		List<String> childList = new ArrayList<String>();
		childList.add(child);
			
		List<TagItem> parentTags = getParentTags(child);;
		
		return(parentTags);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TagItem> getParentTags(List<String> children) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("children", children);
		map.put("size", children.size());
					
		List<TagItem> parentTags =
				(List<TagItem>) session.selectList("database.TagMapper.getParentTags", map);
		
		session.close();
		
		return(parentTags);
	}
	
	
	public void deleteTag(TagItem tag) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		String tagName = tag.getName();
					
		List<TagItem> parentTags = getParentTags(tagName);
						
		for(int i = 0; i < parentTags.size(); i++) {
			
			TagItem parentTag = parentTags.get(i);
			
			parentTag.getLinkedTagNames().remove(tagName);
						
			modifyTag(parentTag, "");
		}
				
		session.delete("database.TagMapper.deleteTag", tagName);
							
		session.close();
	}
	

	public void setTagUsedCounters() {
		
		List<TagItem> tags = getAllTags();
				
		for(int i = 0; i < tags.size(); i++) {
			
			TagItem tag = tags.get(i);
					
			ArrayList<String> searchTags = new ArrayList<String>();
			searchTags.add(tag.getName());
			
			List<MediaItem> temp = mediaTable.getMediaByTagQuery(searchTags);
			
			int count = temp.size();
			
			tag.setUsed(count);
			
			modifyTag(tag, "");
			
		}
		
	}
	
	/*
		
		if tagImageURL is not empty, the image will be downloaded and added
		to the tag
		
		returns true if successful
	*/
	public boolean modifyTag(final TagItem tag, final String tagImageURL) 
	{		
		if(tag.getName().isEmpty()) return(false);
		
		TagItem oldTag = getTagById(tag.getId());
			
		if(oldTag != null) {
							
			// Update the tag			
			boolean linkedTagsModified = updateTag(tag, oldTag);
			
			if(linkedTagsModified) {

				mediaTable.updateLinkedTags(tag);
			}
			
		} else if(getTagByName(tag.getName()) != null) {
			
			// new tag name already exists
			return(false);
			
		} else {

			insertTag(tag);
		}

		storeTagImage(tag, tagImageURL);
		
		return(true);
	}
	
	private void storeTagImage(TagItem tag, String imageURL) {
		
		if(imageURL.equals("")) return;

		URL u;
		InputStream input = null;
		ByteArrayOutputStream output = null;

		try {

			u = new URL(imageURL);
			String filename = u.getFile();
		
			String suffix = ".jpg";
			
			int lastDot = filename.lastIndexOf('.');
						
			if(lastDot != -1) {
			 
				suffix = filename.substring(lastDot).toLowerCase();
			}

			String mimeType = fileUtils.MimeType.getMimeTypeFromExt(suffix);

			if(!mimeType.startsWith("image")) {	
				// dont't know what kind of image this is, just guess it's a jpg
				mimeType = fileUtils.MimeType.getMimeTypeFromExt(".jpg");
			}

			input = new BufferedInputStream(u.openStream());			
			output = new ByteArrayOutputStream();
						
			IOUtils.copy(input, output);	      

			TagImageItem tagImage = new TagImageItem();
			
			tagImage.setTag(tag.getName());
			tagImage.setMimeType(mimeType);
			tagImage.setSizeBytes(output.size());
			tagImage.setImageData(output.toByteArray());
			
			SqlSession session = MyBatis.getSession().openSession(); 
					
			session.insert("database.TagMapper.setTagImage", tagImage);
			
			session.close();
						
		} catch (MalformedURLException mue) {

			System.out.println(mue.getMessage());

		} catch (IOException ioe) {

			System.out.println(ioe.getMessage());

		} finally {

			try {
				if(input != null) input.close();
				if(output != null) output.close();
			} catch (IOException ioe) {

			}

		}
		
	}
	
	public boolean hasTagImage(String tagName) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		Integer result = (Integer) session.selectOne("database.TagMapper.hasTagImage", tagName);
		
		session.close();
		
		return(result != 0 ? true : false);
		
	}
	
	public TagImageItem getTagImage(String tagName) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		TagImageItem tagImage = (TagImageItem) session.selectOne("database.TagMapper.getTagImage", tagName);
		
		session.close();
		
		return(tagImage);
		
	}


	public static void Test() {

	}
	

}


