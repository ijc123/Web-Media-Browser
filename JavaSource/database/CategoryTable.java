package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;

import utils.MapArgument;

import beans.user.LoginBean;

@Stateless
public class CategoryTable {
	
	@EJB
	TypeTable typeTable;
	
	@Inject
	LoginBean loginBean;
	
	@SuppressWarnings("unchecked")
	public List<CategoryItem> getCategoriesByType(String typeName) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(		
				"typeName", typeName,
				"accessTypes", accessTypes
				);
		
		List<CategoryItem> category  = (List<CategoryItem>) session.selectList("database.CategoryMapper.getCategoriesByType", map);
			
		session.close();
		
		return(category);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoryItem> getAllCategories() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(		
				"accessTypes", accessTypes
				);
		
		List<CategoryItem> category = (List<CategoryItem>) session.selectList("database.CategoryMapper.getAllCategories", map);		
		
		session.close();
		
		return(category);
		
	}
	
	public void setCategory(CategoryItem category) {
	
		if(category == null) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.insert("database.CategoryMapper.setCategory", category);
		
		session.close();
	}
	
	public void renameCategory(CategoryItem category, String newName) {
		
		if(category == null) return;
		if(newName == null || newName.isEmpty()) return;
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("typeName", category.getTypeName());
		map.put("name", category.getName());
		map.put("newName", newName);
		
		SqlSession session = MyBatis.getSession().openSession(); 
				
		session.update("database.CategoryMapper.renameCategory", map);
	
		session.close();
	}
	
	public void deleteCategory(CategoryItem category) {
		
		if(category == null) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.delete("database.CategoryMapper.deleteCategory", category);
	
		session.close();
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoryItem> getChildCategories(CategoryItem parent) {
		
		List<CategoryItem> category = null;
		
		if(parent.getFullName().equals("/")) {
			
			List<TypeItem> type = typeTable.getAllTypes();
			
			category = new ArrayList<CategoryItem>();
			
			for(int i = 0; i < type.size(); i++) {
			
				category.add(new CategoryItem("/" + type.get(i).getName()));
			}
			
		} else {
			
			SqlSession session = MyBatis.getSession().openSession(); 
			
			List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
			
			Map<String, Object> map = MapArgument.create(	
					"category", parent,
					"accessTypes", accessTypes
					);
				
			category = (List<CategoryItem>) session.selectList("database.CategoryMapper.getCategoryByPrefix", map);
				
			session.close();
		}
		
		Map<String, CategoryItem> uniqueChildMap = new HashMap<String, CategoryItem>();
		
		for(int i = 0; i < category.size(); i++) {
			
			String fullName = category.get(i).getFullName();
			
			int parentLength = parent.getFullName().length();
			int nextSlash = fullName.indexOf("/", parentLength + 1);
			if(nextSlash == -1) nextSlash = fullName.length();
			
			String child = fullName.substring(parentLength, nextSlash);
		
			if(!uniqueChildMap.containsKey(child) && child.isEmpty() == false) {
				uniqueChildMap.put(child, category.get(i));
			}
			
		}
		
		category.clear();
		
		for(Map.Entry<String, CategoryItem> entry : uniqueChildMap.entrySet())
		{
			category.add(entry.getValue());
		}

		java.util.Collections.sort(category);
		
		return(category);
	
	}
}
