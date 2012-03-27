// http://balusc.blogspot.com/2009/12/uploading-files-in-servlet-30.html
package filters;

import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;


//@WebFilter(urlPatterns = { "/resources/*" })
@WebFilter(urlPatterns = { "/*" })
public class DynamicResourceLoaderFilter implements Filter {

		//private boolean debugOutput = false;
	
	    @Override
	    public void init(FilterConfig config) throws ServletException {
	    	

	        
	    }

	    @Override
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws 
	    	IOException,ServletException
	    {
/*	    	
	        HttpServletRequest httpRequest = (HttpServletRequest) request;
	        	        	        
	        String requestURL = "";
	        
			try {
				
				requestURL = URLDecoder.decode(httpRequest.getRequestURL().toString(), "UTF-8").toLowerCase();
				
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
			}
			
			if(debugOutput) {
        		Output.info(this, "DynamicResourceLoaderFilter: " +  httpRequest.getRequestURL() + " " +
        				httpRequest.getMethod() + " ");
        	}
	
	        if(requestURL.contains("/dynamicresource/images/")) {
	        		        	        	
	        	String fileName = requestURL.substring(requestURL.lastIndexOf("/") + 1, requestURL.lastIndexOf("."));
	        	
	        	response = new ImageResourceResponse((HttpServletResponse) response, fileName);
						        
	        } 
*/
	        	       	        
			chain.doFilter(request, response);					
			
	    }

	    @Override
	    public void destroy() {
	      
	    }

}
