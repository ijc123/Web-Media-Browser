package video;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

import servlet.LoadMediaServlet;
import utils.SystemConstants;
import virtualFile.Location;
import database.MediaItem;

public class FrameGrabber {
	
	public void start(final MediaItem video, final Location output) throws ExecuteException, IOException, URISyntaxException {
					
		// grab a frame every 30 seconds
		int frameTime = 30;
		
		// width of grabbed frames
		int frameWidth = 333;
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				
		String mediaURL = SystemConstants.getLanAdress() + LoadMediaServlet.getMediaDataURL(video, session.getId());
							
		String batch = String.format("framegrabber -i \"%s\" -t %s -s -w %s -o \"%s\"", 
				mediaURL, frameTime, frameWidth, output.getDiskPath());

		CommandLine cmdLine = CommandLine.parse(batch);

		DefaultExecutor executor = new DefaultExecutor();			
		
		// 5 minute watchdog
		ExecuteWatchdog watchdog = new ExecuteWatchdog(300000); 
		executor.setWatchdog(watchdog);

		executor.execute(cmdLine);
		
	}

	
}

