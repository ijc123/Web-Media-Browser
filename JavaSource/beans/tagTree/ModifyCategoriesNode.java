package beans.tagTree;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import database.CategoryTable;


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
			
			CategoryTable categoryTable = null;
			
			try {
				categoryTable = (CategoryTable) new InitialContext().lookup("java:module/CategoryTable");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			categoryTable.renameCategory(category, temp + "/" + shortName);
			
			category.setShortName(shortName);
				
		} 
		
		
	}
	

}
