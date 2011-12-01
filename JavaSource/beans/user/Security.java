package beans.user;

import javax.enterprise.event.Observes;
import javax.faces.application.NavigationHandler;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.Before;
import org.jboss.seam.faces.event.qualifier.RenderResponse;

public class Security {

	@Inject
	LoginBean loginSession;
	
	public void observeBefore(@Observes @Before PhaseEvent e)
	{
		// Listens to all phases, before the phase
		if(loginSession != null) {
			
			boolean loggedIn = loginSession.isLoggedIn();
			
		}
		
	}
	 
	public void observeAfter(@Observes @After @RenderResponse PhaseEvent e, NavigationHandler navHandler)
	{
		// Listens to the RenderResponse phase, after the phase
		
	
	}
	
}
