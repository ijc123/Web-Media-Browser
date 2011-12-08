package beans.tagTree;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import database.CategoryEJB;


public class ModifyCategoriesNode extends Node<ModifyCategoriesNode> {
	
	public ModifyCategoriesNode(String path) {
		
		super(path, ModifyCategoriesNode.class);
        
	}
	
	public boolean isRootNode() {
		
		return(false);
	}
	
	@Override
	public void setShortName(String shortName) {
		
		if(!shortName.equals(category.getShortName())) {
			
			// update category name
			String oldCategory = category.getName();
			String temp = oldCategory.substring(0, oldCategory.lastIndexOf('/')); 
			
			CategoryEJB categoryEJB = null;
			
			try {
				categoryEJB = (CategoryEJB) new InitialContext().lookup("java:module/CategoryEJB");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			categoryEJB.renameCategory(category, temp + "/" + shortName);
			
			category.setShortName(shortName);
				
		} 
		
		
	}
	

}
