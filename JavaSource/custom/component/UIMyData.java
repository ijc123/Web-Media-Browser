package custom.component;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

@FacesComponent("custom.component.UIMyData")
public class UIMyData extends UIData {

	protected int myFirst;
	
	public UIMyData() {
		
		super();
		
		myFirst = 0;
	}

	@Override
	public void setFirst(int first) {
		
		myFirst = first;
		
		super.setFirst(first);
		
	}
	
	@Override
	public Object saveState(FacesContext arg0) {
		
		//int first = getFirst();
		
		return(super.saveState(arg0));
	}
	
	@Override
	public void restoreState(FacesContext arg0, Object arg1) {
		
		super.restoreState(arg0, arg1);
/*	
		for(int i = 0; i < getRows(); i++) {
			
			setRowIndex(i);
			MediaTableItem rowData = (MediaTableItem) getRowData();
						
			if(rowData.isSelected()) {
				
				int j = 0;
			}
			
		}		
*/
		
		//int first = getFirst();
	}
	
}
