package beans.tagTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

@RequestScoped
@Named
public class TagPickerBean {
	
    private List<TagPickerNode> rootNodes;
    private String selectedCategory;
    private String selectedTag;

    public synchronized List<TagPickerNode> getRootNodes() {
    	
        if (rootNodes == null) {
                		
        	rootNodes = new TagPickerNode("/").getNodes();
        }

        return rootNodes;
    }
    
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
        	
        	sessionMap.put("TagPickerBean.selectedTag", selectedTag);
        
        } else {
        
        	selectedCategory = ((TagPickerNode) temp).getCategory().getFullName();
        	selectedTag = null;
        	
        	sessionMap.put("TagPickerBean.selectedCategory", selectedCategory);
        	
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
