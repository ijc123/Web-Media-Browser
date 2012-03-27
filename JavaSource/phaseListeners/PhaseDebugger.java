package phaseListeners;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import debug.Output;

public class PhaseDebugger implements PhaseListener {

	private static final long serialVersionUID = 1L;

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	@Override
	public void afterPhase(PhaseEvent event) {


		
	}

	public void beforePhase(PhaseEvent event) {
			
		Output.info(this, "Current phase: " + event.getPhaseId());					
			
	}

	protected UIComponent findComponent(UIComponent c, String clientId) {
		
		
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
