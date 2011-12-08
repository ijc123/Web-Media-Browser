package beans.tagTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import database.CategoryItem;
import database.CategoryEJB;
import database.TypeItem;
import database.TypeEJB;


@RequestScoped
@Named
public class ModifyCategoriesBean {
	
	@Inject
	CategoryEJB categoryEJB;
	@Inject
	TypeEJB typeEJB;
	
    private List<Node<ModifyCategoriesNode>> rootNodes;
    private String selectedCategory;
    private String selectedTag;

    public synchronized List<Node<ModifyCategoriesNode>> getRootNodes() {
    	
        if (rootNodes == null) {
                	        	
        	rootNodes = new ArrayList<Node<ModifyCategoriesNode>>();
        	
        	List<TypeItem> rootType = typeEJB.getAllTypes();
        	
        	for(int i = 0; i < rootType.size(); i++) {
        	
        		rootNodes.add(new ModifyCategoriesRootNode("/" + rootType.get(i).getName()));
        	}
        }

        return rootNodes;
    }
    
    public void createCategory() {
    	
    	Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    	
    	@SuppressWarnings("unchecked")
		Node<ModifyCategoriesNode> selectedNode = (Node<ModifyCategoriesNode>)sessionMap.get("ModifyCategoriesBean.selectedCategory");
    	
    	if(selectedNode == null) return;
    	
    	CategoryItem parent = selectedNode.getCategory();
    	
    	List<CategoryItem> child = categoryEJB.getChildCategories(parent);
        	
    	String uniqueCategory = "category";
    	
    	int j = 0;
    	
    	for(int i = 0; i < child.size(); i++) {
    		
    		if((uniqueCategory + Integer.toString(j)).equals(child.get(i).getShortName())) {
    			
    			j++;
    			i = 0;
    		}
    		
    	}
    	    	    
    	uniqueCategory = parent.getFullName() + "/" + uniqueCategory + Integer.toString(j);
    	
    	CategoryItem newCategory = new CategoryItem(uniqueCategory);
    	
    	categoryEJB.setCategory(newCategory);
    		
		// force a update of the tree model during render response 
		rootNodes = null;
    }
    
    public void deleteCategory() {
    	
    	Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    	
    	@SuppressWarnings("unchecked")
		Node<ModifyCategoriesNode> selectedNode = 
			(Node<ModifyCategoriesNode>)sessionMap.get("ModifyCategoriesBean.selectedCategory");
    	 	
    	if(selectedNode == null || selectedNode.getClass().getName().equals("beans.tagTree.ModifyCategoriesRootNode")) return;
    	    	    	
    	categoryEJB.deleteCategory(selectedNode.getCategory());	
    	
		// force a update of the tree model during render response 
		rootNodes = null;
    }
    
    
    @SuppressWarnings("unchecked")
	public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        // considering only single selection
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();
        
        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);
        Object temp = tree.getRowData();
        
        String className = temp.getClass().getName();
        
        Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        
        if(className.equals("java.lang.String")) {
        	      	
        	selectedCategory = null;
        	selectedTag = (String) temp;
        	
        	sessionMap.put("ModifyCategoriesBean.selectedTag", selectedTag);
        
        } else {
        
        	selectedCategory = ((Node<ModifyCategoriesNode>) temp).getCategory().getFullName();
        	selectedTag = null;
        	
        	sessionMap.put("ModifyCategoriesBean.selectedCategory", temp);
        	
        }
       
        tree.setRowKey(storedKey);
    }

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public String getSelectedTag() {
		return selectedTag;
	}
    
    
}
