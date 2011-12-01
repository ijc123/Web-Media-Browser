package custom.component;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

@FacesComponent("custom.component.QueryTable")
public class UIQueryTable extends UIOutput {
	
	public enum SortOrder {
		ASCENDING, DECENDING
	}
	
	protected Map<String, SortOrder> columnSortOrder;
	protected String orderByColumn;

	public UIQueryTable() {
		
		super();
				
		orderByColumn = "fileName";
		columnSortOrder = new HashMap<String, SortOrder>();
		
		columnSortOrder.put("fileName", SortOrder.ASCENDING);
		
		setRendererType("custom.component.querytable.QueryTable");
	}
	
	@Override
	public Object saveState(FacesContext context) {
		
		Object[] values = new Object[3];
		
		values[0] = super.saveState(context);
		values[1] = columnSortOrder;
		values[2] = orderByColumn;
		
		return(values);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {
		
		Object[] values = (Object[])state;
		
		super.restoreState(context, values[0]);
		columnSortOrder = (Map<String, SortOrder>) values[1];
		orderByColumn= (String) values[2];		
	}
	
	public void setOrderByColumn(String column) {
	
		orderByColumn= column;
		
		SortOrder order = columnSortOrder.get(column);
		
		if(order == null) {
			
			columnSortOrder.put(column, SortOrder.ASCENDING);
		
		} else {
		
			if(order == SortOrder.ASCENDING) {
				
				columnSortOrder.put(column, SortOrder.DECENDING);
				
			} else {
				
				columnSortOrder.put(column, SortOrder.ASCENDING);
			}
		}
	}
		
	public String getOrderByColumn() {
		
		return(orderByColumn);
	}

	public SortOrder getColumnSortOrder() {
	
		return(columnSortOrder.get(orderByColumn));
	}
	
}
