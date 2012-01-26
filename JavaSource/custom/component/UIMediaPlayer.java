package custom.component;

import java.io.IOException;

import javax.faces.application.ViewHandler;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import video.HTTPLiveStreaming;
import database.MediaItem;
import database.MediaEJB;


@FacesComponent("custom.component.UIMediaPlayer")
public class UIMediaPlayer extends UIOutput {

	static video.Transcode transcode;
	
	//private String uri;
	private String id;
	private String localHost;
	private Boolean autostart;
	private Boolean rendered;
	private Integer width;
	private Integer height;

	static {

		transcode = null;
	}

	public UIMediaPlayer() {

		super();
		//uri = "";
		autostart = false;
		rendered = true;
		id = "mediaPlayer";
		width = 640;
		height = 480;
		localHost = "0:0:0:0:0:0:0:1";
	}
	
	private String getMediaDataUrl(String path) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String actionURL = handler.getActionURL(context, "/loaddatasegment").replace(".jsf", "");
		
		String location = actionURL + "?path=" + path;
		
		return(location);
	}

	protected void renderFireFox(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart, String ipAddress) throws IOException 
	{

		String location;
		
		if(ipAddress.equals(localHost)) {
		
			// we are on the local machine, no need to transcode the video's 
			// just load them directly
			location = media.getPath();
			
		} else {
			
			location = getMediaDataUrl(media.getPath());
		}
		
		rw.startElement("embed", this);

		rw.writeAttribute("id", clientId + ":" + id, null);
		rw.writeAttribute("type","application/x-mplayer2",null);

		rw.writeAttribute("src", location, null);
		rw.writeAttribute("width", width.toString(), null);
		rw.writeAttribute("height", height.toString(), null);

		rw.writeAttribute("showcontrols", "1", null);
		rw.writeAttribute("showstatusbar", "0", null);
		rw.writeAttribute("showdisplay", "0", null);

		rw.writeAttribute("autostart", autostart == true ? "1" : "0", null);

		rw.endElement("embed");

	}

	protected void renderInternetExplorer(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart, String ipAddress) throws IOException 
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
		
		String location;
		
		if(ipAddress.equals(localHost)) {
		
			// we are on the local machine, no need to stream the video's 
			// just load them directly
			//location = media.getPath();
			location = getMediaDataUrl(media.getPath());
			
		} else {
			
			location = getMediaDataUrl(media.getPath());
		}
		
		rw.startElement("object", this);

		rw.writeAttribute("id", clientId + ":" + id, null);
		rw.writeAttribute("classid","CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6",null);
		
		rw.writeAttribute("width", width.toString(), null);
		rw.writeAttribute("height", height.toString(), null);
		
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
		rw.writeAttribute("value", autostart == true ? "1" : "0", null);
		rw.endElement("param");

		rw.endElement("object");

	}

	protected void renderSafari(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart, String ipAddress) throws IOException 
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
		
		String location = getMediaDataUrl(media.getPath());
				
		rw.startElement("object", this);

		rw.writeAttribute("id", clientId + ":" + id, null);
		rw.writeAttribute("classid","clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B",null);
		
		rw.writeAttribute("width", width.toString(), null);
		rw.writeAttribute("height", height.toString(), null);
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
		rw.writeAttribute("value", autostart == true ? "1" : "0", null);
		rw.endElement("param");

		////////////////////////////////
		
		rw.startElement("embed", this);

		rw.writeAttribute("src", location, null);
		rw.writeAttribute("width", width.toString(), null);
		rw.writeAttribute("height", height.toString(), null);
		rw.writeAttribute("autoplay", autostart == true ? "1" : "0", null);
		rw.writeAttribute("controller", "1", null);
		rw.writeAttribute("loop", "0", null);
		rw.writeAttribute("pluginspage", "http://www.apple.com/quicktime/download/", null);
		
		rw.endElement("embed");
		
		rw.endElement("object");

	}
	
	protected void renderMobileSafari(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart) throws IOException 
	{

		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String actionURL = handler.getActionURL(context, "/iosvideo");

		String location = "http://192.168.1.4:8080" + actionURL + "?id=";
		//String outputDir = "g://transcode//";

		HTTPLiveStreaming.publish(media.getPath(), location);
		/*		
		if(transcode != null) {

			transcode.stopRunning();
		}

		transcode = new video.Transcode("Transcoding Thread", media.getPath(), 
				outputDir, location);
		//transcode = new video.Transcode("Transcoding Thread", media.getPath(), 
		//		outputDir + "output.ts");
		transcode.start();
		 */		
		rw.startElement("video", this);
		rw.writeAttribute("src", location + "index.m3u8", null);
		rw.writeAttribute("height", "300", null);
		rw.writeAttribute("width", "400", null);


		rw.endElement("video");

	}
	
	protected void renderHTML5Video(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart, String ipAddress) throws IOException {
		/*
		<video width="320" height="240" controls="controls">
		  <source src="movie.mp4" type="video/mp4" />
		  <source src="movie.ogg" type="video/ogg" />
		  Your browser does not support the video tag.
		</video>
	 	*/
		
		String location = getMediaDataUrl(media.getPath());
		
		rw.startElement("video", this);
		
		rw.writeAttribute("controls", "controls", null);
		rw.writeAttribute("width", width.toString(), null);
		rw.writeAttribute("height", height.toString(), null);
		
		rw.startElement("source", this);	
		rw.writeAttribute("src", location, null);
		rw.writeAttribute("type", "video/mp4", null);
		rw.endElement("source");
	
		rw.endElement("video");
		
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		if(rendered == false) return;
		
		String clientId = getClientId(context);

		ResponseWriter rw = context.getResponseWriter();
		
		String uri = (String) getAttributes().get("uri");
/*
		String 
		String id = (String) getAttributes().get("id");
		if(id == null) id = "mediaPlayer";

		Boolean autostart = Boolean.parseBoolean((String) getAttributes().get("autostart"));

		Boolean rendered = Boolean.parseBoolean((String) getAttributes().get("rendered"));
*/
		MediaEJB mediaEJB = null;

		try {
			mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		MediaItem media = mediaEJB.getMediaByUri(uri);
		
		HttpServletRequest request=
				(HttpServletRequest) context.getExternalContext().getRequest();

		//The user-agent string contains information about which
		//browser is used to view the pages
		String useragent = request.getHeader("user-agent").toLowerCase();
		
		// get client ip adresss
		String ipAddress = getClientIpAddr(request);

		if(useragent.indexOf("msie") != -1) {
			
			//renderHTML5Video(rw, clientId, id, media, autostart, ipAddress);

			renderInternetExplorer(rw, clientId, id, media, autostart, ipAddress);

		} else if(useragent.indexOf("firefox") != -1) {
		
			renderFireFox(rw, clientId, id, media, autostart, ipAddress);

		} else if(useragent.indexOf("mobile") != -1 && useragent.indexOf("safari") != -1) { 

			renderMobileSafari(rw, clientId, id, media, autostart);
			
		} else if(useragent.indexOf("safari") != -1) {
	
			//renderHTML5Video(rw, clientId, id, media, autostart, ipAddress);
			renderSafari(rw, clientId, id, media, autostart, ipAddress);
			
		} else {
			
			renderFireFox(rw, clientId, id, media, autostart, ipAddress);
			
		}


	}
/*
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
*/
	public Boolean getAutostart() {
		return autostart;
	}

	public void setAutostart(Boolean autostart) {
		this.autostart = autostart;
	}

	public Boolean getRendered() {
		return rendered;
	}

	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
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
