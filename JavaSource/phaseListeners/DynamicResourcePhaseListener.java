package phaseListeners;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import utils.MimeType;
import video.HTTPLiveStreamingXuggler;
import virtualFile.Location;
import database.MediaEJB;
import database.TagEJB;
import database.ImageItem;
import debug.Log;


public class DynamicResourcePhaseListener implements PhaseListener {

	private static final long serialVersionUID = 1L;

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

			Log.info(this, "Loading Tag Image: " + tagName);

			TagEJB tagEJB = null;

			try {
				tagEJB = (TagEJB) new InitialContext().lookup("java:module/TagEJB");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ImageItem tagImage = tagEJB.getTagImage(tagName);

			if(tagImage != null) {

				generateBinaryResponse(context, servletResponse, tagImage.getImageData(), tagImage.getMimeType());
			}

		} else if(viewId.startsWith("/previewimage")) {

			String path = external.getRequestParameterMap().get("id");

			Log.info(this, "Loading Preview Image: " + path);

			generateBinaryResponse(context, servletResponse, path, "image/jpeg");

		} else if(viewId.startsWith("/video")) {

			String path = external.getRequestParameterMap().get("id");

			Log.info(this, "Loading Video: " + path);

			String mimeType = MimeType.getMimeTypeFromExt(path);

			if (mimeType == null) return;

			generateBinaryResponse(context, servletResponse, path, mimeType);

		} else if(viewId.startsWith("/iosvideo")) {

			String file = external.getRequestParameterMap().get("id");

			String path = HTTPLiveStreamingXuggler.getOutputDir() + file;

			//String path = "g:/transcode/" + file;

			Log.info(this, "Loading IOS Video Segment: " + path);

			if(file.endsWith("m3u8")) {

				generateBinaryResponse(context, servletResponse, path, "application/x-mpegURL");

			} else {

				generateBinaryResponse(context, servletResponse, path, "video/MP2T");

			}

		} else if (viewId.startsWith("/thumbnail")) {

			String uri = external.getRequestParameterMap().get("id");

			Log.info(this, "Loading Thumbnail Image: " + uri);

			MediaEJB mediaEJB = null;

			try {
				mediaEJB = (MediaEJB) new InitialContext().lookup("java:module/MediaEJB");
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ImageItem thumbnail = mediaEJB.getMediaThumbnail(uri);

			if(thumbnail != null) {

				generateBinaryResponse(context, servletResponse, thumbnail.getImageData(), thumbnail.getMimeType());
			}

		} 

	}

	public void beforePhase(PhaseEvent event) {

	}

	private void generateBinaryResponse(FacesContext context, HttpServletResponse servletResponse, String path, String mimeType) {

		InputStream input = null;

		try {

			Location location = new Location(path);
			
			File dataFile = new File(location.getDiskPath());

			if(!dataFile.exists()) {

				Log.info(this, path + " not found");

				servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, path + " not found");

				context.responseComplete();
				return;
			}

			input = new FileInputStream(dataFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generateBinaryResponse(context, servletResponse, input, mimeType);

	}

	private void generateBinaryResponse(FacesContext context, HttpServletResponse servletResponse, byte[] data, String mimeType) {

		InputStream	input = new ByteArrayInputStream(data);

		generateBinaryResponse(context, servletResponse, input, mimeType);
	}

	private void generateBinaryResponse(FacesContext context, HttpServletResponse servletResponse, InputStream input, String mimeType) {

		OutputStream output = null;

		try {

			servletResponse.setContentType(mimeType);

			output = servletResponse.getOutputStream();

			int maxbufferSize = 16384; // 128kb

			byte[] buffer = new byte[maxbufferSize];

			int bytesRead = 0;
			int sizeBytes = 0;

			while((bytesRead = input.read(buffer)) != -1) {

				output.write(buffer, 0, bytesRead);
				sizeBytes += bytesRead;
			}

			servletResponse.setContentLength(sizeBytes);
			context.responseComplete();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if(input != null) {

				try {

					input.close();

				} catch (IOException e) {

					e.printStackTrace();
				}
			}

			if(output != null) {

				try {

					output.close();

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}

	} 
}
