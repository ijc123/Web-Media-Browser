package video;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import beans.user.LoggedIn;

import servlet.LoadMediaServlet;
import utils.SystemConstants;
import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import virtualFile.Location;
import database.MediaItem;
import database.UserItem;
import debug.Log;

public class HTTPLiveStreaming2 {

	@Inject @LoggedIn UserItem user;
	
	private ExecuteWatchdog segmenterWatchdog; 
	private ExecuteWatchdog transcoderWatchdog; 
		
	static String ipodLowResSettings = "-vcodec libx264 -b 250k -bt 50k -s 480x320 -acodec libvo_aacenc";
	static String ipodHighResSettings = "-vcodec libx264 -b 250k -bt 50k -s 640x480 -acodec libvo_aacenc";

	static String outputPath = "C:/Java/jboss-as-web-7.0.2.Final/bin/";
	
	public enum Profile {
		IPOD_LOW_RES,
		IPOD_HIGH_RES,
	}
	
	public HTTPLiveStreaming2() {
		
		segmenterWatchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		transcoderWatchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);		
	}
	
	// returns the index filename
	public synchronized void publish(MediaItem media, Location publishURL, 
			Profile profile) throws IOException 
	{

		if(!media.isVideo()) return;
		
		
		if(isAlreadyTranscoded(media)) {
			
			return;
		}			
		
		PipedOutputStream pipedOutput = new PipedOutputStream();
		PumpStreamHandler pipedOutputHandler = new PumpStreamHandler(pipedOutput, System.err, System.in);
		
		PipedInputStream pipedInput = new PipedInputStream(pipedOutput);
		PumpStreamHandler pipedInputHandler = new PumpStreamHandler(System.out, System.err, pipedInput);
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			
		String videoURL = SystemConstants.getLanAdress() + LoadMediaServlet.getMediaDataURL(media, session.getId());
		
		String arguments;

		switch(profile) {

			case IPOD_HIGH_RES: {
	
				arguments = ipodHighResSettings;
				break;
			}
	
			case IPOD_LOW_RES: {
				arguments = ipodLowResSettings;
				break;
			}
		
			default: {
			
				throw new IOException("Unsupported profile supplied");				
			}
		}

		try {
									
			String transcoder = String.format("avconv -i %s %s -f mpegts pipe:", 
					videoURL, arguments);
			
			String outputFilename = getOutputFilePrefix(media);
			String indexFilename = getIndexFileName(media);
			String url = publishURL.getEncodedURL();
			
			String segmenter = String.format("segmenter - 10 %s %s %s", 
					outputFilename, indexFilename, url);			
									
			runBatchProcess(segmenter, segmenterWatchdog, pipedInputHandler);
			runBatchProcess(transcoder, transcoderWatchdog, pipedOutputHandler);
			

		} catch (ExecuteException e) {

			e.printStackTrace();

		} 

	}

	private void runBatchProcess(
			String batchCommand, 
			ExecuteWatchdog watchdog, 
			ExecuteStreamHandler streamHandler) throws ExecuteException, IOException {

		CommandLine cmdLine = CommandLine.parse(batchCommand);

		DefaultExecutor executor = new DefaultExecutor();

		if(streamHandler != null) {
			executor.setStreamHandler(streamHandler);
		}

		if(watchdog != null) {
			executor.setWatchdog(watchdog);
		}

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

		executor.execute(cmdLine, resultHandler);

	}

	private boolean isAlreadyTranscoded(MediaItem media) {

		try {

			FileUtils f = FileUtilsFactory.create(getOutputPath());

			String indexFileName = getIndexFileName(media);

			if(f.exists(indexFileName)) {

				BufferedReader indexFile;

				String indexFilePath = getOutputPath() + indexFileName;

				indexFile = new BufferedReader(new FileReader(indexFilePath));

				String endMarker = "#EXT-X-ENDLIST";
				String line;

				while((line = indexFile.readLine()) != null) {

					if(line.equals(endMarker)) {
						indexFile.close();
						Log.info("video.HTTPLiveStreaming", "Video done transcoding: " + media.getFileName());
						// the video has completed transcoding						
						return(true);
					}
				} 

				indexFile.close();

				// the video is in the process of being transcoded
				if(segmenterWatchdog.isWatching() ||
					transcoderWatchdog.isWatching()) {

					Log.info("video.HTTPLiveStreaming", "Video in the process of transcoding: " + media.getFileName());
					return(true);
					
				}
							
			}

			Log.info("video.HTTPLiveStreaming", "start transcoding: " + media.getFileName());
			
			// delete previously transcoded video data	
			Stop();			
					
			ArrayList<FileInfo> tsFiles = new ArrayList<FileInfo>();

			f.getDirectoryContents(tsFiles, "*.ts");
			
			for(int i = 0; i < tsFiles.size(); i++) {

				f.deleteFile(tsFiles.get(i).getName());
			}
			
			ArrayList<FileInfo> indexFiles = new ArrayList<FileInfo>();
			
			f.getDirectoryContents(indexFiles, "*.m3u8");
			
			for(int i = 0; i < indexFiles.size(); i++) {

				f.deleteFile(indexFiles.get(i).getName());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return(false);

	}

	public void Stop() {
		
		// another video is being transcoded, 
		// stop previously running processes
		if(transcoderWatchdog.isWatching()) {		
			transcoderWatchdog.destroyProcess();
		}
		
		if(segmenterWatchdog.isWatching()) {
			segmenterWatchdog.destroyProcess();
		}	
		
	}
	
	private String getOutputFilePrefix(MediaItem media) {

		String userHash = Integer.toString(user.getName().hashCode());
		String mediaId = Integer.toString(media.getId());
		
		return(userHash + mediaId);

	}

	public String getIndexFileName(MediaItem media) {

		return(getOutputFilePrefix(media) + ".m3u8");
	}
	
	public static String getOutputPath() {
		
		return(outputPath);
	}
} 




