package beans.user;

import java.io.IOException;
import java.io.Serializable;

//import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import database.UserItem;
import database.UserEJB;

@SessionScoped
@Named
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 7965455427888195913L;

	@Inject
    private CredentialsBean credentials;

    @Inject
    private UserEJB userEJB;
    
    @Inject
    private FacesContext facesContext;

    private UserData currentUser;

    public String login() throws Exception {
       
       UserItem user = userEJB.getUser(credentials.getUsername(), credentials.getPassword());
       if (user != null) {
    	   
          currentUser = new UserData(user);
          
          // store the logged in user in the httpsession 
          HttpSession session = (HttpSession)facesContext.getExternalContext().getSession(false);
          session.setAttribute("loggedInUser", currentUser);
                    
          return("login success");
          
       } else {
    	   
    	   facesContext.addMessage(null, new FacesMessage("Login Failed"));
    	   return("login failure");
       }
    }

    public void logout() {

    	// remove loggedinuser from httpsession
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		session.removeAttribute("loggedInUser");

		facesContext.addMessage(null, new FacesMessage("Goodbye, " + currentUser.getUserItem().getName()));
		
		// stop any transcoding for this user
		currentUser.getHttpLiveTranscoder().Stop();
		currentUser = null;

		try {
			facesContext.getExternalContext().redirect("/mijngod/pages/login.jsf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public boolean isLoggedIn() {
       return currentUser != null;
    }

    @Produces @LoggedIn
    public UserItem getCurrentUserItem() {
    
    	if(currentUser == null) return(null);
    	else return(currentUser.getUserItem());
    }
    
    public UserData getCurrentUserData() {
        
    	return(currentUser);
    }

    public void updateCurrentUserItem() {
    	
    	currentUser.setUserItem(userEJB.getUserByName(currentUser.getUserItem().getName()));
    }
}
