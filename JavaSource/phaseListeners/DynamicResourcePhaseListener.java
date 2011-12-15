package phaseListeners;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import utils.MimeType;
import video.HTTPLiveStreaming;
import database.TagEJB;
import database.TagImageItem;


public class DynamicResourcePhaseListener implements PhaseListener {
	
	private static final long serialVersionUID = 1L;
	private static final boolean debugOutput = true;

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	@Override
	public void afterPhase(PhaseEvent event) {

		FacesContext context = event.getFacesContext();
		ExternalContext external = event.getFacesContext().getExternalContext();
		HttpServletResponse servletResponse = (HttpServletResponse) external.getResponse();
		
		String viewId = event.getFacesContext().getViewRoot().getViewId();
		
		if (viewId.startsWith("/tagimage")) {
						
			String tagName = external.getRequestParameterMap().get("id");
			
			if(debugOutput) System.out.println("Loading Tag Image: " + tagName);
			
			TagEJB tagEJB = null;
			
			try {
				tagEJB = (TagEJB) new InitialContext().lookup("java:module/TagEJB");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			TagImageItem tagImage = tagEJB.getTagImage(tagName);
				
			if(tagImage != null) {
			
				generateBinaryResponse(context, servletResponse, tagImage.getImageData(), tagImage.getMimeType());
			}
		
		} else if(viewId.startsWith("/previewimage")) {
						
			String path = external.getRequestParameterMap().get("id");
			
			if(debugOutput) System.out.println("Loading Preview Image: " + path);
				
			generateBinaryResponse(context, servletResponse, path, "image/jpeg");
			
		} else if(viewId.startsWith("/video")) {
			
			String path = external.getRequestParameterMap().get("id");
			
			if(debugOutput) System.out.println("Loading Video: " + path);
			
			String mimeType = MimeType.getMimeTypeFromExt(path);
			
			if (mimeType == null) return;
				
			generateBinaryResponse(context, servletResponse, path, mimeType);
			
		} else if(viewId.startsWith("/iosvideo")) {
		
			String file = external.getRequestParameterMap().get("id");
			
			String path = HTTPLiveStreaming.getOutputDir() + file;
						
			//String path = "g:/transcode/" + file;
			
			if(debugOutput) System.out.println("Loading IOS Video Segment: " + path);
			
			if(file.endsWith("m3u8")) {
				
				generateBinaryResponse(context, servletResponse, path, "application/x-mpegURL");
			
			} else {
				
				generateBinaryResponse(context, servletResponse, path, "video/MP2T");
				
			}
			
		}
		
	}

	public void beforePhase(PhaseEvent event) {

	}

	void generateBinaryResponse(FacesContext context, HttpServletResponse servletResponse, String path, String mimeType) {
						
		InputStream in = null;
		OutputStream out = null;
		
		try {
			
			File dataFile = new File(path);
			
			if(!dataFile.exists()) {
				
				System.out.println("DynamicResourcePhaseListner: " + path + " not found");
				servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, path + " not found");
				context.responseComplete();
				return;
			}
			
			
			servletResponse.setContentType(mimeType);
			
			in = new FileInputStream(dataFile);
			out = servletResponse.getOutputStream();
			
			int length = (int) dataFile.length();
			int maxbufferSize = 16384; // 128kb
			
			byte[] buffer = new byte[Math.min(length, maxbufferSize)];

			int bytesRead = 0;
			
			while((bytesRead = in.read(buffer)) != -1) {
			    out.write(buffer, 0, bytesRead);
			}
			
			servletResponse.setContentLength(length);
			context.responseComplete();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
		
			e.printStackTrace();
			
		} finally {
			
			if(in != null) {
				
				try {
					
					in.close();
					
				} catch (IOException e) {
		
					e.printStackTrace();
				}
			}
			
			if(out != null) {
				
				try {
					
					out.close();
					
				} catch (IOException e) {
			
					e.printStackTrace();
				}
				
			}
		}
	
	}
	
	void generateBinaryResponse(FacesContext context, HttpServletResponse servletResponse, byte[] data, String mimeType) {
		
		InputStream in = null;
		OutputStream out = null;
		
		try {
					
			servletResponse.setContentType(mimeType);
			
			in = new ByteArrayInputStream(data);
			out = servletResponse.getOutputStream();
			
			int length = (int) data.length;
			int maxbufferSize = 16384; // 128kb
			
			byte[] buffer = new byte[Math.min(length, maxbufferSize)];

			int bytesRead = 0;
			
			while((bytesRead = in.read(buffer)) != -1) {
			    out.write(buffer, 0, bytesRead);
			}
			
			servletResponse.setContentLength(length);
			context.responseComplete();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
		
			e.printStackTrace();
			
		} finally {
			
			if(in != null) {
				
				try {
					
					in.close();
					
				} catch (IOException e) {
		
					e.printStackTrace();
				}
			}
			
			if(out != null) {
				
				try {
					
					out.close();
					
				} catch (IOException e) {
			
					e.printStackTrace();
				}
				
			}
		}
	
	}
}
