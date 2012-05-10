package servlet;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import virtualFile.Location;
import database.MediaEJB;
import database.MediaItem;
import database.MediaPreviewEJB;
import database.SettingsEJB;
import debug.Log;

public class LoadResourceServlet extends LoadDataSegmentServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	MediaEJB mediaEJB;
	
	@Inject
	SettingsEJB settingsEJB;
	
	private String previewRootDirectory;
	
	@Override
	public void init() {
		
		previewRootDirectory = settingsEJB.getSettings().getPreviewRootDirectory();
	}
	
	@Override
	protected InputData getInputData(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		//Get a media item
		String mediaId = request.getParameter("mediaId");

		// Do your thing if the file is not supplied to the request URL.
		// Throw an exception, or send 404, or show default/warning page, or just ignore it.        	
		if(mediaId == null) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			Log.error(this, "no mediaId specified");
			return(null);

		} 

		int id = Integer.parseInt(mediaId);

		MediaItem media = mediaEJB.getMediaById(id);
		
		if(media == null) {

			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			Log.error(this, "media forbidden");
			return(null);       			
		}
		
		try {
			
			Location previewLocation = new Location(previewRootDirectory);
			
			String previewPath = MediaPreviewEJB.getMediaPreviewPath(media);
			
			previewLocation.moveDown(previewPath);
			
			previewLocation.setFilename(media.getFileName());
			
			previewLocation.setExtension("jpg");
			
			InputData input = new InputData(previewLocation, media.getFileName(), 
					media.getSizeBytes(), 0, true);
				
			return(input);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		Log.error(this, "error accessing resource");
		return(null);
	}

	public static String getMediaPreviewURL(MediaItem media) {

		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String actionURL = handler.getActionURL(context, "/loadresource").replace(".jsf", "");

		String url = actionURL + "?mediaId=" + Integer.toString(media.getId());

		return(url);
	}
}
