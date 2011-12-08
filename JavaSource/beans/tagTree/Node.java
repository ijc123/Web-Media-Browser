package beans.tagTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import database.CategoryItem;
import database.CategoryEJB;

// uses reflection to instantiate nodes of different types, see link below for more info
// http://java.sun.com/developer/technicalArticles/ALT/Reflection/
public class Node<Type>  {

	private Constructor<Type> typeConstructor;
	
	protected List<Type> nodes;
	protected CategoryItem category;
	
	protected Node(String path, Class<Type> clazz) {
			
		try {
			
			// get the node constructor with a string argument
			typeConstructor = clazz.getConstructor(new Class[] {String.class});
			
		} catch (SecurityException e) {
			
			e.printStackTrace();
			
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}
		
		category = new CategoryItem(path);
	
	}
		
	private Type newNodeTypeInstance(String path) {
		
		Type node = null;
		
		try {
			
			Object[] arguments = new Object[1];
			arguments[0] = new String(path);
			
			node = typeConstructor.newInstance(arguments);
			
		} catch (InstantiationException e) {
			
			e.printStackTrace();
			
		} catch (IllegalAccessException e) {

			e.printStackTrace();
			
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
			
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}
		
		return(node);
	}
	
	public synchronized List<Type> getNodes() {
		
		if(nodes != null) return(nodes);
		
		nodes = new ArrayList<Type>();
		
		CategoryEJB categoryEJB = null;
		
		try {
			categoryEJB = (CategoryEJB) new InitialContext().lookup("java:module/CategoryEJB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<CategoryItem> child = categoryEJB.getChildCategories(category);
				
		for(int i = 0; i < child.size(); i++) {
			
			Type node = newNodeTypeInstance(child.get(i).getFullName());
						
			nodes.add(node);
		
		}
				
		return(nodes);
	}
	
	public void setShortName(String shortName) {

		category.setShortName(shortName);
	}
	
	public String getShortName() {
		
		return(category.getShortName());
		
	}
	
	public CategoryItem getCategory() {
		
		return(category);
	}
	

}	
