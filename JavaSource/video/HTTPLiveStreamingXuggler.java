package video;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import virtualFile.FileInfo;
import virtualFile.FileUtilsLocal;



public class HTTPLiveStreamingXuggler {

	static ExecuteWatchdog segmenterWatchdog;
	static Transcode transcode;
	static String outputDir;

	static {

		segmenterWatchdog = null;
		transcode = null;
		outputDir = "C:/Java/jboss-as-web-7.0.2.Final/bin/";
	}

	public static String getOutputDir() {

		return outputDir;

	}

	public static void publish(String inputFile, String location) throws IOException {

		if(segmenterWatchdog != null) {

			segmenterWatchdog.destroyProcess();

		} else {

			segmenterWatchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		}

		PipedOutputStream pipedOutput = new PipedOutputStream();

		if(transcode != null) {

			transcode.stopRunning();
		}

		if(isAlreadyTranscoded(inputFile)) {

			return;
		}

		transcode = new Transcode("transcode", inputFile, pipedOutput);

		PipedInputStream pipedInput = new PipedInputStream(pipedOutput);
		PumpStreamHandler pipedInputHandler = new PumpStreamHandler(System.out, System.err, pipedInput);

		try {

			String outputDir = "h:/segmenter/";
			String segmenter = String.format("%ssegmenter - 10 video_segment index.m3u8 %s", outputDir, location);			

			runBatchProcess(segmenter, segmenterWatchdog, pipedInputHandler);			
			transcode.start();

		} catch (ExecuteException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}


	}

	private static void runBatchProcess(
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

	private static Boolean isAlreadyTranscoded(String inputFile) {
	
		String prevTranscoded = "";
		
		String prevTranscodedFileName = getOutputDir() + "transcoded.txt";

		File prevTranscodedFile = new File(prevTranscodedFileName);

		if(prevTranscodedFile.exists()) {

			try {
				BufferedReader transcodedFile = new BufferedReader(new FileReader(prevTranscodedFileName));
				prevTranscoded = transcodedFile.readLine();
				transcodedFile.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 

		}
			
		if(prevTranscoded.equals(inputFile)) {
			
			BufferedReader indexFile;
			try {
				indexFile = new BufferedReader(new FileReader(getOutputDir() + "index.m3u8"));
				String endMarker = "#EXT-X-ENDLIST";
				String line;
				
				while((line = indexFile.readLine()) != null) {
					
					if(line.equals(endMarker)) {
						indexFile.close();
						return(true);
					}
				} 
								
				indexFile.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			
			try {
				
				BufferedWriter newTranscodedFile = new BufferedWriter(new FileWriter(prevTranscodedFileName));
				newTranscodedFile.write(inputFile);
				newTranscodedFile.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}

/*		
		File index = new File(getOutputDir() + "index.m3u8");

		if(index.exists()) {

			index.delete();
		}
*/
				
		try {

			FileUtilsLocal f = new FileUtilsLocal(getOutputDir());
			
			ArrayList<FileInfo> oldTsFiles = new ArrayList<FileInfo>();
			
			f.getDirectoryContents(oldTsFiles, "*.ts");

			for(int i = 0; i < oldTsFiles.size(); i++) {
							
				f.deleteFile(oldTsFiles.get(i).getName());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return(false);

	}
} 




