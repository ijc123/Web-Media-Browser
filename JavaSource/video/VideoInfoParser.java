package video;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import servlet.LoadDataSegmentServlet;
import utils.SystemConstants;
import database.MediaItem;

public class VideoInfoParser {

	public VideoInfo start(final MediaItem video) throws ExecuteException, IOException {
				
		String port = Integer.toString(SystemConstants.getJbossPort());
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		
		String mediaURL = "http://127.0.0.1:" + port + LoadDataSegmentServlet.getMediaDataURL(video, session.getId());
		
		String batch = String.format("avprobe -show_format -show_streams -loglevel quiet \"%s\"", mediaURL);

		CommandLine cmdLine = CommandLine.parse(batch);

		DefaultExecutor executor = new DefaultExecutor();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		executor.setWatchdog(watchdog);
			
		executor.setStreamHandler(streamHandler);
		
		executor.execute(cmdLine);
		
		VideoInfo info = parseAVProbeOutput(outputStream.toString());
		
		return(info);
		
	}
		
	private void parseValue(String property, Scanner lineScanner, VideoInfo info) throws InputMismatchException {
		
		if(property.equals("filename")) {
			
			info.setFilename(lineScanner.next());
			
		} else if(property.equals("nb_streams")) {
			
			info.setNrStreams(lineScanner.nextInt());
			
		} else if(property.equals("format_name")) {
			
			info.setFormatName(lineScanner.next());
			
		} else if(property.equals("format_long_name")) {
			
			info.setFormatLongName(lineScanner.next());
		
		} else if(property.equals("start_time")) {
			
			info.setStartTimeSeconds(Double.parseDouble(lineScanner.next()));
			
		} else if(property.equals("duration")) {
			
			String value = lineScanner.next();
			
			if(value.equals("N/A")) return;
			
			info.setDurationSeconds(Double.parseDouble(value));
			
		} else if(property.equals("bit_rate")) {
				
			info.setBitRate((int)Double.parseDouble(lineScanner.next()));
			
		} else if(property.equals("size")) {
			
			info.setSizeBytes((int)Double.parseDouble(lineScanner.next()));
			
		} else if(property.equals("TAG:encoder")) {
			
			info.setEncoderTag(lineScanner.next());
			
		} else if(property.equals("width")) {
			
			info.setWidth(lineScanner.nextInt());
		
		} else if(property.equals("height")) {
			
			info.setHeight(lineScanner.nextInt());
			
		} 
	}
	
	private VideoInfo parseAVProbeOutput(String avprobeOutput) {
		
		VideoInfo info = new VideoInfo();
		
		Scanner outputScanner = new Scanner(avprobeOutput);
			
		while(outputScanner.hasNextLine()) {
			
			String line = outputScanner.nextLine();
			
			Scanner lineScanner = new Scanner(line).useDelimiter("=");
										
			if(lineScanner.hasNext()) {
				
				String property = lineScanner.next();
				parseValue(property, lineScanner, info);
			}
						
		}
		
		return(info);
	}
}
