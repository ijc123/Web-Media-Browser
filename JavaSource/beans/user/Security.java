package beans.user;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

import org.jboss.seam.faces.event.qualifier.After;
import org.jboss.seam.faces.event.qualifier.Before;
import org.jboss.seam.faces.event.qualifier.RestoreView;

public class Security {

	@Inject
	LoginBean loginSession;
	
	@Inject 
	ExternalContext externalContext;
	
	@Inject
	FacesContext facesContext;
	
	public void observeBefore(@Observes @Before PhaseEvent e)
	{
	
	}
	 
	public void observeAfter(@Observes @After @RestoreView PhaseEvent e, NavigationHandler navHandler)
	{
		String loginPage = "/pages/login.xhtml";
		String redirectPage = "/mijngod/pages/login.jsf";
		
		// Listens to the RenderResponse phase, after the phase
		boolean loggedIn = loginSession.isLoggedIn();
		
		String currentPage = facesContext.getViewRoot().getViewId();
		
		if(!loggedIn && !currentPage.equals(loginPage)) {
			
			try {
				externalContext.redirect(redirectPage);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	}
	
}
