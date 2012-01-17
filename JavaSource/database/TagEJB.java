package database;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.jboss.ejb3.annotation.IgnoreDependency;

import utils.ImageUtil;
import utils.MapArgument;
import beans.user.LoginBean;

@Stateless
public class TagEJB {

	@EJB
	@IgnoreDependency
	private MediaEJB mediaEJB;
	
	@Inject
	private LoginBean loginBean;
			
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
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(
				"name", name,
				"accessTypes", accessTypes
				);
		
		results = (List<TagItem>) session.selectList("database.TagMapper.getTagsByName", map);
			
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
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(
				"category", category,
				"accessTypes", accessTypes
				);
		
		List<TagItem> tag = (List<TagItem>) session.selectList("database.TagMapper.getTagsByCategory", map);
		
		session.close();
		
		return(tag);		
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TagItem> getAllTags() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(
				"accessTypes", accessTypes
				);
		
		List<TagItem> tag = (List<TagItem>) session.selectList("database.TagMapper.getAllTags", map);
		
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
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(
				"name", name + "%",
				"accessTypes", accessTypes
				);
		
		List<TagItem> tag = (List<TagItem>) session.selectList("database.TagMapper.getTagsByNameQuery", map);
			
		session.close();
		
		return(tag);		
	}
		
	private boolean updateTag(TagItem newTagItem, final TagItem oldTagItem) {
						
		if(newTagItem.equals(oldTagItem)) {
			
			return(false);
			
		} else {
			
			newTagItem.setVersion(oldTagItem.getVersion() + 1);
		}
		
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
	
	private void insertTag(TagItem newTagItem) {
							
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.insert("database.TagMapper.insertTag", newTagItem);								
	
		session.close();
		
		setTagLinked(newTagItem);
		setTagCategory(newTagItem);
		
		// update tag id to latest version
		newTagItem.setId(getTagByName(newTagItem.getName()).getId());
	
	}
	
	private void setTagLinked(final TagItem tagItem) {
		
		List<String> linkedTags = tagItem.getLinkedTagNames();
		
		if(linkedTags == null || linkedTags.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
									
		for(int i = 0; i < linkedTags.size(); i++) {
			
			Map<String, Object> map = MapArgument.create(
					"tag", tagItem.getName(),
					"linked", linkedTags.get(i)
					);
					
			session.insert("database.TagMapper.setTagLinked", map);
			
		}
			
		session.close();
	}

	private void deleteTagLinked(String tag, final List<String> linkedTags) {
		
		if(linkedTags.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
					
		for(int i = 0; i < linkedTags.size(); i++) {
			
			Map<String, Object> map = MapArgument.create(			
				"tag", tag,
				"linked", linkedTags.get(i)
				);
		
			session.delete("database.TagMapper.deleteTagLinked", map);
			
		}

		session.close();
	}
		
	private void setTagCategory(final TagItem tagItem) {
		
		List<CategoryItem> category = tagItem.getCategory();
		if(category == null || category.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(ExecutorType.BATCH); 
							
		for(int i = 0; i < category.size(); i++) {
			
			Map<String, Object> map = MapArgument.create(
				"tag", tagItem.getName(),
				"category", category.get(i).getName(),
				"type", category.get(i).getTypeName()
				);
		
			session.insert("database.TagMapper.setTagCategory", map);
			
		}
				
		session.close();
	}
	
	private void deleteTagCategory(String name, final List<CategoryItem> category) {
	
		if(category.isEmpty()) return;
				
		SqlSession session = MyBatis.getSession().openSession(); 
					
		for(int i = 0; i < category.size(); i++) {
			
			Map<String, Object> map = MapArgument.create(
					"tag", name,
					"category", category.get(i).getName(),
					"type", category.get(i).getTypeName()
					);
		
			session.delete("database.TagMapper.deleteTagCategory", map);
			
		}				

		session.close();
	}
	
	public List<TagItem> getParentTags(String child) {
		
		List<String> childList = new ArrayList<String>();
		childList.add(child);
			
		List<TagItem> parentTags = getParentTags(childList);
		
		return(parentTags);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TagItem> getParentTags(List<String> children) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(
				"children", children, 
				"size", children.size(),
				"accessTypes", accessTypes
				);
							
		List<TagItem> parentTags =
				(List<TagItem>) session.selectList("database.TagMapper.getParentTags", map);
		
		session.close();
		
		return(parentTags);
	}
	
	
	public void deleteTag(TagItem tag) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
							
		String tagName = tag.getName();
	
		session.delete("database.TagMapper.deleteTag", tagName);
							
		session.close();
	}
	
	public void deleteTagsWithoutCategory() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.delete("database.TagMapper.deleteTagsWithoutCategory");
							
		session.close();
		
	}
	

	public void setTagUsedCounters() {
		
		List<TagItem> tags = getAllTags();
				
		for(int i = 0; i < tags.size(); i++) {
			
			TagItem tag = tags.get(i);
					
			ArrayList<String> searchTags = new ArrayList<String>();
			searchTags.add(tag.getName());
			
			List<MediaItem> temp = mediaEJB.getMediaByTagQuery(searchTags);
			
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
	public boolean modifyTag(TagItem tag, final String tagImageURL) 
	{		
		if(tag.getName().isEmpty()) return(false);
		
		TagItem oldTag = getTagById(tag.getId());
			
		if(oldTag != null) {
							
			// Update the tag			
			boolean linkedTagsModified = updateTag(tag, oldTag);
			
			if(linkedTagsModified) {

				mediaEJB.updateLinkedTags(tag);
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
		
		if(imageURL == null || imageURL.equals("")) return;

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

			String mimeType = utils.MimeType.getMimeTypeFromExt(suffix);

			if(!mimeType.startsWith("image")) {	
				// dont't know what kind of image this is, just guess it's a jpg
				mimeType = utils.MimeType.getMimeTypeFromExt(".jpg");
			}

			input = new BufferedInputStream(u.openStream());			
			output = new ByteArrayOutputStream();
						
			IOUtils.copy(input, output);	      
		
			ImageItem tagImage;
			
			try {
				
				ImageUtil imageUtil = new ImageUtil();
				
				tagImage = imageUtil.createThumbNail(output.toByteArray(), 200);
				tagImage.setOwner(tag.getName());
				
			} catch (Exception e) {
				
				tagImage = new ImageItem();
				
				tagImage.setOwner(tag.getName());
				tagImage.setMimeType(mimeType);
				tagImage.setSizeBytes(output.size());
				tagImage.setImageData(output.toByteArray());
				
				e.printStackTrace();
			}
						
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
	
	public ImageItem getTagImage(String tagName) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		ImageItem tagImage = (ImageItem) session.selectOne("database.TagMapper.getTagImage", tagName);
		
		session.close();
		
		return(tagImage);
		
	}


	public void Test() {

		List<TagItem> tags = getAllTags();
		
		for(int i = 0; i < tags.size(); i++) {
		
			TagItem tag = tags.get(i);
			
			if(!tag.getLinkedTagNames().isEmpty()) {
				
				modifyTag(tag, null);
			}
				
		}
		
	}
	

}


