package webListener;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import debug.Output;

@WebListener
public class WebDebugListener implements HttpSessionListener {

	// stores all active httpsessions
    private static ConcurrentHashMap<String, Object> sessions;

    static {
    	
    	sessions = new ConcurrentHashMap<String, Object>();
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        
    	Output.info(this, "session created: " + event.getSession().getId());
           
    	sessions.put(event.getSession().getId(), event.getSession());
            
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    	
    	Output.info(this, "session destroyed: " + event.getSession().getId());
       
        sessions.remove(event.getSession().getId());
    }

    public static HttpSession getSession(String sessionId) {
    	    	
    	return((HttpSession)sessions.get(sessionId));
    
    }

}

