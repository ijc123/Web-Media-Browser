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
	
	static {
		
		transcode = null;
	}
	
	public UIMediaPlayer() {
		
		super();
		
	}
	
	protected void renderFireFox(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart) throws IOException 
	{
		
		rw.startElement("embed", this);
			
		rw.writeAttribute("id", clientId + ":" + id, null);
		rw.writeAttribute("type","application/x-mplayer2",null);
		
		rw.writeAttribute("src", media.getPath(), null);
		rw.writeAttribute("width", "640", null);
		rw.writeAttribute("height", "480", null);
		
		rw.writeAttribute("showcontrols", "1", null);
		rw.writeAttribute("showstatusbar", "0", null);
		rw.writeAttribute("showdisplay", "0", null);
						
		rw.writeAttribute("autostart", autostart == true ? "1" : "0", null);

		rw.endElement("embed");
		
	}
	
	protected void renderInternetExplorer(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart) throws IOException 
	{
		
		rw.startElement("object", this);
			
		rw.writeAttribute("id", clientId + ":" + id, null);
		rw.writeAttribute("classid","CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6",null);
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "url", null);
		rw.writeAttribute("value", media.getPath(), null);
		rw.endElement("param");
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "width", null);
		rw.writeAttribute("value", "800", null);
		rw.endElement("param");
		
		rw.startElement("param", this);	
		rw.writeAttribute("name", "height", null);
		rw.writeAttribute("value", "600", null);
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
		rw.writeAttribute("name", "autostart", null);
		rw.writeAttribute("value", autostart == true ? "1" : "0", null);
		rw.endElement("param");
					
		rw.endElement("object");
		
	}
	
	protected void renderSafari(ResponseWriter rw, String clientId, String id, MediaItem media, 
			Boolean autostart) throws IOException {
		
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
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		
		String clientId = getClientId(context);

		ResponseWriter rw = context.getResponseWriter();
		
		String uri = (String) getAttributes().get("uri");
		
		MediaEJB mediaEJB = null;
		
		try {
			mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MediaItem media = mediaEJB.getMediaByUri(uri);
		
		String id = (String) getAttributes().get("id");
		if(id == null) id = "mediaPlayer";
		
		Boolean autostart = Boolean.parseBoolean((String) getAttributes().get("autostart"));
		
		 HttpServletRequest request=
		     (HttpServletRequest) context.getExternalContext().getRequest();
		 
		   //The user-agent string contains information about which
		   //browser is used to view the pages
		 String useragent = request.getHeader("user-agent").toLowerCase();
		
		  if (useragent.indexOf("msie")!=-1) {
			  			
			  renderInternetExplorer(rw, clientId, id, media, autostart);
		  
		  } else if (useragent.indexOf("firefox")!=-1) {
			  
			  //renderSafari(rw, clientId, id, media, autostart);
			  renderFireFox(rw, clientId, id, media, autostart);
		  
		  } else { 
			
			  //renderInternetExplorer(rw, clientId, id, media, autostart);
			  //renderFireFox(rw, clientId, id, media, autostart);
			  renderSafari(rw, clientId, id, media, autostart);
			
		  }


	}
	
}
