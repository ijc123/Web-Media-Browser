package servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.MediaEJB;
import database.MediaItem;
import database.UserItem;
import debug.Log;

import virtualFile.VirtualInputFile;
import virtualFile.VirtualInputFileFactory;
import webListener.WebDebugListener;

public class LoadMediaServlet extends LoadDataSegmentServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	MediaEJB mediaEJB;

	@Override
	protected VirtualInputFile getInputFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession(false);

		UserItem loggedInUser = null;

		if(session == null) {

			// no active httpsession found for this request

			String sessionId = request.getParameter("sessionId");

			if(sessionId == null) {

				// no httpsession parameter supplied for this request either

				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				Log.error(this, "trying to download resource without a valid session");
				return(null);

			} else {

				// httpsession supplied for this request, look it up

				session = WebDebugListener.getSession(sessionId);

				if(session == null) {

					Log.error(this, "supplied sessionId: " + sessionId + " not in sessionMap");
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return(null);
				}

				loggedInUser = (UserItem)session.getAttribute("loggedInUser");

				if(loggedInUser == null) {

					// user is not logged in

					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					Log.error(this, "trying to download resource when user is not logged in");
					return(null);
				}

			}
		}


		// Validate the requested file ------------------------------------------------------------


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

		MediaItem media = null;

		if(loggedInUser != null) {

			media = mediaEJB.getMediaById(id, loggedInUser);

		} else {

			media = mediaEJB.getMediaById(id);
		}

		if(media == null) {

			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			Log.error(this, "media forbidden");
			return(null);       			
		}

		VirtualInputFile inputFile = null;

		try {
			inputFile = VirtualInputFileFactory.create(media.getUri());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return(inputFile);
	}

	public static String getMediaDataURL(MediaItem media) {

		return(getMediaDataURL(media,null));
	}

	public static String getMediaDataURL(MediaItem media, String sessionId) {

		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String actionURL = handler.getActionURL(context, "/loadmedia").replace(".jsf", "");

		String url = actionURL + "?mediaId=" + Integer.toString(media.getId());

		if(sessionId != null) {

			try {

				sessionId = URLEncoder.encode(sessionId, "UTF-8");
				url += "&sessionId=" + sessionId;

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return(url);
	}

}
