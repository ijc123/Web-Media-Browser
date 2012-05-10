package beans.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	
	private Map<String, Integer> pageAccessLevel;
	
	public Security() {
		
		pageAccessLevel = new HashMap<String, Integer>();
		
		pageAccessLevel.put("/pages/settings.xhtml", 1);
		pageAccessLevel.put("/pages/createtype.xhtml", 1);
	}
	
	public void observeBefore(@Observes @Before PhaseEvent e)
	{
	
	}
	 
	public void observeAfter(@Observes @After @RestoreView PhaseEvent e, NavigationHandler navHandler)
	{
		
		// If we are not logged in, redirect to login page
		boolean loggedIn = loginSession.isLoggedIn();
		
		String currentPage = facesContext.getViewRoot().getViewId();
		
		if(!loggedIn && !currentPage.equals("/pages/login.xhtml")) {
			
			try {
				externalContext.redirect("/mijngod/pages/login.jsf");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		// Check if we have the correct accesslevel to acces this page
		Integer i = pageAccessLevel.get(currentPage);
		
		if(i != null && i != loginSession.getCurrentUser().getLevel()) {
			
			try {
				externalContext.redirect("/mijngod/pages/accessdenied.jsf");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		}
		
	}
	
}
