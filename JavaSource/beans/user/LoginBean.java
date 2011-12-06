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

import database.UserItem;
import database.UserTable;

@SessionScoped
@Named
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 7965455427888195913L;

	@Inject
    private CredentialsBean credentials;

    @Inject
    private UserTable userTable;

    private UserItem currentUser;

    public String login() throws Exception {
       
       UserItem user = userTable.getUser(credentials.getUsername(), credentials.getPassword());
       if (user != null) {
    	   
          this.currentUser = user;
          return("login success");
          
       } else {
    	   
    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login Failed"));
    	   return("login failure");
       }
    }

    public void logout() {
       FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Goodbye, " + currentUser.getName()));
       currentUser = null;
       
       try {
    	   FacesContext.getCurrentInstance().getExternalContext().redirect("/mijngod/pages/login.jsf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public boolean isLoggedIn() {
       return currentUser != null;
    }

    @Produces
    public UserItem getCurrentUser() {
       return currentUser;
    }

    
}
