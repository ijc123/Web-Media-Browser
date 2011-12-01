package beans.tagTree;


public class ModifyCategoriesRootNode extends Node<ModifyCategoriesNode> {

	
	public ModifyCategoriesRootNode(String path) {
		
		super(path, ModifyCategoriesNode.class);
				
	}
	
	public boolean isRootNode() {
		
		return(true);
	}
	
	
}
