package custom.component;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import servlet.HTTPLiveStreamingServlet;
import servlet.LoadMediaServlet;
import utils.SystemConstants;
import video.HTTPLiveStreaming;
import virtualFile.Location;
import beans.user.UserData;
import database.MediaEJB;
import database.MediaItem;


@FacesComponent("custom.component.UIMediaPlayer")
public class UIMediaPlayer extends UIOutput {


	protected enum Properties {
		id,
		mediaId,
		autoStart, 
		rendered,
		width, 
		height
	}
	
	//private String uri;
	//private static String localHost = "0:0:0:0:0:0:0:1";

	public UIMediaPlayer() {

		super();
		
	}
	
	protected void renderFireFox(ResponseWriter rw, String id, MediaItem media, 
			String ipAddress, FacesContext context) throws IOException 
	{
			
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		
		String location = LoadMediaServlet.getMediaDataURL(media, session.getId());
	
		rw.startElement("embed", this);

		rw.writeAttribute("id", id, null);
		rw.writeAttribute("type","application/x-mplayer2",null);

		rw.writeAttribute("src", location, null);
		rw.writeAttribute("width", Integer.toString(getWidth()), null);
		rw.writeAttribute("height", Integer.toString(getHeight()), null);

		rw.writeAttribute("showcontrols", "1", null);
		rw.writeAttribute("showstatusbar", "0", null);
		rw.writeAttribute("showdisplay", "0", null);

		rw.writeAttribute("autostart", isAutoStart() == true ? "1" : "0", null);

		rw.endElement("embed");

	}

	protected void renderInternetExplorer(ResponseWriter rw, String id, MediaItem media, 
			String ipAddress) throws IOException 
	{
		/*
		 	<OBJECT ID="vidObj" WIDTH=320 HEIGHT=240 CLASSID="CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6">
				<PARAM NAME="AutoStart" VALUE="0">
				<PARAM NAME="ShowStatusBar" VALUE="0">
				<PARAM NAME="EnableContextMenu" VALUE="1">
				<PARAM NAME="WindowlessVideo" VALUE="0">
				<PARAM NAME="uiMode" VALUE="none">
				<PARAM NAME="stretchToFit" VALUE="1">
				<PARAM NAME="AutoSize" VALUE="1">
				<PARAM NAME="Filename" VALUE="">
				<PARAM NAME="ShowControls" VALUE="0">
				<PARAM NAME="SendMouseClickEvents" VALUE="1">
			</OBJECT>
		 */
		
		String location = LoadMediaServlet.getMediaDataURL(media);
		
		rw.startElement("object", this);

		rw.writeAttribute("id", id, null);
		rw.writeAttribute("classid","CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6",null);
		
		rw.writeAttribute("width", Integer.toString(getWidth()), null);
		rw.writeAttribute("height", Integer.toString(getHeight()), null);
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "url", null);
		rw.writeAttribute("value", location, null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "showcontrols", null);
		rw.writeAttribute("value", "1", null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "showstatusbar", null);
		rw.writeAttribute("value", "0", null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "showdisplay", null);
		rw.writeAttribute("value", "0", null);
		rw.endElement("param");
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "stretchToFit", null);
		rw.writeAttribute("value", "1", null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "windowlessVideo", null);
		rw.writeAttribute("value", "1", null);
		rw.endElement("param");
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "autostart", null);
		rw.writeAttribute("value", isAutoStart() == true ? "1" : "0", null);
		rw.endElement("param");

		rw.endElement("object");

	}

	protected void renderSafari(ResponseWriter rw, String id, MediaItem media, 
			String ipAddress) throws IOException 
	{
		/*
		 	<OBJECT classid='clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B' width="320" height="240" codebase='http://www.apple.com/qtactivex/qtplugin.cab'>
			<param name='src' value="http://mydomain.com/video.mov">
			<param name='autoplay' value="true">
			<param name='controller' value="false">
			<param name='loop' value="false">
			<EMBED src="http://mydomain.com/video.mov" width="320" height="240" autoplay="true" 
			controller="false" loop="false" bgcolor="#000000" pluginspage='http://www.apple.com/quicktime/download/'>
			</EMBED>
			</OBJECT>
		 */
		
		String location = LoadMediaServlet.getMediaDataURL(media);
				
		rw.startElement("object", this);

		rw.writeAttribute("id", id, null);
		rw.writeAttribute("classid","clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B",null);
		
		rw.writeAttribute("width", Integer.toString(getWidth()), null);
		rw.writeAttribute("height", Integer.toString(getHeight()), null);
		rw.writeAttribute("codebase", "http://www.apple.com/qtactivex/qtplugin.cab", null);
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "src", null);
		rw.writeAttribute("value", location, null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "controller", null);
		rw.writeAttribute("value", "1", null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "loop", null);
		rw.writeAttribute("value", "0", null);
		rw.endElement("param");

		rw.startElement("param", this);	
		rw.writeAttribute("name", "autoplay", null);
		rw.writeAttribute("value", isAutoStart() == true ? "1" : "0", null);
		rw.endElement("param");

		////////////////////////////////
		
		rw.startElement("embed", this);

		rw.writeAttribute("src", location, null);
		rw.writeAttribute("width", Integer.toString(getWidth()), null);
		rw.writeAttribute("height", Integer.toString(getHeight()), null);
		rw.writeAttribute("autoplay", isAutoStart() == true ? "1" : "0", null);
		rw.writeAttribute("controller", "1", null);
		rw.writeAttribute("loop", "0", null);
		rw.writeAttribute("pluginspage", "http://www.apple.com/quicktime/download/", null);
		
		rw.endElement("embed");
		
		rw.endElement("object");

	}
	
	protected void renderMobileSafari(ResponseWriter rw, String id, MediaItem media,
			 FacesContext context) throws IOException 
	{

		Location publishURL;
		
		try {
			
			publishURL = new Location(SystemConstants.getWanAdress() + HTTPLiveStreamingServlet.getURL());
			
			HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
			
			UserData userData = (UserData)session.getAttribute("currentUser");
			
			HTTPLiveStreaming transcoder = userData.getHttpLiveTranscoder();
			
			transcoder.publish(media, publishURL, HTTPLiveStreaming.Profile.IPOD_HIGH_RES, userData.getUserItem());
			
			String indexURL = publishURL.getEncodedURL() + transcoder.getIndexFileName(userData.getUserItem(), media);
			
			rw.startElement("video", this);
			rw.writeAttribute("src", indexURL, null);
			rw.writeAttribute("height", "300", null);
			rw.writeAttribute("width", "400", null);

			rw.endElement("video");
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

	}
	
	protected void renderHTML5Video(ResponseWriter rw, String id, MediaItem media, 
			String ipAddress) throws IOException {
		/*
		<video width="320" height="240" controls="controls">
		  <source src="movie.mp4" type="video/mp4" />
		  <source src="movie.ogg" type="video/ogg" />
		  Your browser does not support the video tag.
		</video>
	 	*/
		
		String location = LoadMediaServlet.getMediaDataURL(media);
		
		rw.startElement("video", this);
		
		rw.writeAttribute("controls", "controls", null);
		rw.writeAttribute("width", Integer.toString(getWidth()), null);
		rw.writeAttribute("height", Integer.toString(getHeight()), null);
		
		rw.startElement("source", this);	
		rw.writeAttribute("src", location, null);
		rw.writeAttribute("type", "video/mp4", null);
		rw.endElement("source");
	
		rw.endElement("video");
		
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		if(isRendered() == false) return;
		
		String id = getClientId(context) + ":" + getId();

		ResponseWriter rw = context.getResponseWriter();
		
		MediaEJB mediaEJB = null;

		try {
			mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		MediaItem media = mediaEJB.getMediaById(getMediaId());
		
		HttpServletRequest request=
				(HttpServletRequest) context.getExternalContext().getRequest();

		//The user-agent string contains information about which
		//browser is used to view the pages
		String useragent = request.getHeader("user-agent").toLowerCase();
		
		// get client ip adresss
		String ipAddress = getClientIpAddr(request);

		if(useragent.indexOf("msie") != -1) {
			
			//renderHTML5Video(rw, clientId, id, media, autostart, ipAddress);

			renderInternetExplorer(rw, id, media, ipAddress);

		} else if(useragent.indexOf("firefox") != -1) {
		
			renderFireFox(rw, id, media, ipAddress, context);

		} else if(useragent.indexOf("mobile") != -1 && useragent.indexOf("safari") != -1) { 

			renderMobileSafari(rw, id, media, context);
			
		} else if(useragent.indexOf("safari") != -1) {
	
			//renderHTML5Video(rw, clientId, id, media, autostart, ipAddress);
			renderSafari(rw, id, media, ipAddress);
			
		} else {
			
			renderFireFox(rw, id, media, ipAddress, context);
			
		}


	}

	public void setMediaId(int mediaId) {
		
		getStateHelper().put(Properties.mediaId, mediaId);
	}
	
	public int getMediaId() {
		
		int value = (Integer)getStateHelper().eval(Properties.mediaId);
		return value;
	}

	public void setAutoStart(boolean autoStart) {
		
		getStateHelper().put(Properties.autoStart, autoStart);
		
	}
	
	public boolean isAutoStart() {
		
		boolean value = (Boolean)getStateHelper().eval(Properties.autoStart, false);
		return value;
	
	}

	public void setRendered(boolean rendered) {
		
		getStateHelper().put(Properties.rendered, rendered);
		
	}
	
	public boolean isRendered() {
		
		boolean value = (Boolean)getStateHelper().eval(Properties.rendered, true);
		return value;
		
	}

	public void setId(String id) {
		
		getStateHelper().put(Properties.id, id);
		
	}
	
	public String getId() {
		
		String value = (String)getStateHelper().eval(Properties.id, "mediaPlayer");
		return value;
	}

	public void setWidth(int width) {
		
		getStateHelper().put(Properties.width, width);
	}
	
	public int getWidth() {
		
		int value = (Integer)getStateHelper().eval(Properties.width, 640);
		return value;
	}

	public void setHeight(int height) {
		
		getStateHelper().put(Properties.height, height);

	}
	
	public int getHeight() {
		
		int value = (Integer)getStateHelper().eval(Properties.height, 480);
		return value;
		
	}

	private String getClientIpAddr(HttpServletRequest request) {
		
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        	
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        	
            ip = request.getHeader("WL-Proxy-Client-IP");            
        }  
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        	
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        	
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        	
            ip = request.getRemoteAddr();  
        }  
        
        return ip;  
    } 
}
