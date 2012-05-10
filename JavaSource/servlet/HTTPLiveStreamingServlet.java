package servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import debug.Log;

import video.HTTPLiveStreaming;
import virtualFile.Location;
import virtualFile.VirtualInputFile;
import virtualFile.VirtualInputFileFactory;

public class HTTPLiveStreamingServlet extends LoadDataServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected VirtualInputFile getInputFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		
		String file = request.getParameter("file");
		
		if(file == null) {
			
			Log.error(this, "No file attribute specified");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return(null);
			
		}
		
		Location inputLocation = null;
		VirtualInputFile inputFile = null;
		
		try {
			
			inputLocation = new Location(HTTPLiveStreaming.getOutputPath() + file);
			
			Log.info(this, "Requesting: " + inputLocation.getDecodedURL());
						
			inputFile = VirtualInputFileFactory.create(inputLocation);
			
		} catch (URISyntaxException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (FileNotFoundException e) {
			
			if(inputLocation.getExtension().equals("m3u8")) {
			
				// a index file is requested but is not (yet) ready on disk
				// send a temporary empty response
				Log.error(this, "Sending empty response, file not found: " + file);
				createEmptyResponse(file, response);
			
			} else {
				
				e.printStackTrace();
			}
		}

		return(inputFile);
	}

	private void createEmptyResponse(String filename, HttpServletResponse response) {
		
		response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType("application/x-mpegURL");
        response.setHeader("Content-Length", "0");
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");	
		
	}
	
	public static String getURL() {

		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String actionURL = handler.getActionURL(context, "/httplivestreaming").replace(".jsf", "");

		String url = actionURL + "?file=";
		
		return(url);
	}
	
	
	
}
