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

	private VideoInfo videoInfo;
	
	public void start(final MediaItem video, final Location output, int frameWidth, int nrFrames) throws ExecuteException, IOException, URISyntaxException {
		
		VideoInfoParser videoInfoParser = new VideoInfoParser();
		
		videoInfo = videoInfoParser.start(video);
		
		float scale = frameWidth / (float)videoInfo.getWidth();
		int frameHeight = (int)(videoInfo.getHeight() * scale);
		
		String frameRes = Integer.toString(frameWidth) + "x" + Integer.toString(frameHeight);
			
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				
		String mediaURL = SystemConstants.getLanAdress() + LoadMediaServlet.getMediaDataURL(video, session.getId());
		
		output.setFilename(video.getFileName());
		
		for(int i = 0; i < nrFrames; i++) {

			int frameTimeSeconds = (int)(videoInfo.getDurationSeconds() / nrFrames * i); 
			
			String frameNr = String.format("%04d", Integer.valueOf(i));
						
			String outputFrameName = output.getDiskPathWithouthFilename() +
					output.getFilenameWithoutExtension() + frameNr + ".png";
						
			String batch = String.format("avconv -ss %s -i \"%s\" -vframes 1 -loglevel quiet -s %s \"%s\"", 
					Integer.toString(frameTimeSeconds), mediaURL, frameRes, outputFrameName);

			CommandLine cmdLine = CommandLine.parse(batch);

			DefaultExecutor executor = new DefaultExecutor();			
			
			ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
			executor.setWatchdog(watchdog);

			executor.execute(cmdLine);

		}
		
	}

	public VideoInfo getVideoInfo() {
		
		return videoInfo;
	}

	
}
