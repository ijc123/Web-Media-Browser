package phaseListeners;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class PhaseDebugger implements PhaseListener {

	private static final long serialVersionUID = 1L;
	private static final boolean debugOutput = true;

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	@Override
	public void afterPhase(PhaseEvent event) {


		
	}

	public void beforePhase(PhaseEvent event) {

		if(debugOutput) {
			
			System.out.println("Current phase: " + event.getPhaseId());
			
		}
		
/*		
		if(event.getPhaseId() ==  PhaseId.APPLY_REQUEST_VALUES){
			
			Map<String, String> req = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			for (Map.Entry<String, String> entry : req.entrySet())
			{
			    System.out.println("KEY: " + entry.getKey() + " VALUE: " + entry.getValue());
			}

			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
			
			UIData data = (UIData)findComponent(root, "testForm:taggableQueryTable:queryTable:table");
			
			int first = data.getFirst();
			System.out.println("first: " + Integer.toString(first));
			int rows = data.getRows();
			System.out.println("rows: " + Integer.toString(rows));
			
			//data.setFirst(10);
			return;
		}
		
		if(event.getPhaseId() ==  PhaseId.PROCESS_VALIDATIONS){
			
			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
			
			UIData data = (UIData)findComponent(root, "testForm:taggableQueryTable:queryTable:table");
			
						
			int first = data.getFirst();
			System.out.println("first: " + Integer.toString(first));
			int rows = data.getRows();
			System.out.println("rows: " + Integer.toString(rows));
						
			//data.setFirst(10);
			
			return;
		}
		

		if(event.getPhaseId() == PhaseId.UPDATE_MODEL_VALUES) {
		
			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
			
			UIData data = (UIData)findComponent(root, "testForm:taggableQueryTable:queryTable:table");
						
			//data.setRowIndex(10);
			
			//HtmlSelectBooleanCheckbox checkbox = (HtmlSelectBooleanCheckbox) findComponent(root, "testForm:taggableQueryTable:queryTable:table:10:selected");
			
			//boolean selected = checkbox.isSelected();
						
			int first = data.getFirst();
			System.out.println("first: " + Integer.toString(first));
			int rows = data.getRows();
			System.out.println("rows: " + Integer.toString(rows));
			
			//data.setFirst(10);
						
			return;
		}
	*/	
	
	}

	protected UIComponent findComponent(UIComponent c, String clientId) {
		
		//System.out.println(c.getClientId());
		
		if (clientId.equals(c.getClientId())) {
			return c;
		}
				
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), clientId);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

}
