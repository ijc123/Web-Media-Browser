package video;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

public class Transcode extends Thread {

	private class CoderInfo {

		public IStreamCoder coder;
		public int streamIndex;	

		public CoderInfo(IStreamCoder coder, int streamIndex) {

			this.coder = coder;
			this.streamIndex = streamIndex;			
		}

	}
	
	private class SegmentInfo {
		
		int segmentNr;
		double prevSegmentTime;
		int segmentLengthSeconds;
		@SuppressWarnings("unused")
		int prevBufPos;
		String outputDir;
		String segmentFilePrefix;
		String indexFileName;
		String publishURL;
		
		public SegmentInfo(String outputDir, String publishURL) {
			
			this.publishURL = publishURL;
			this.outputDir = outputDir;
			segmentNr = 0;
			prevSegmentTime = 0.0;
			segmentLengthSeconds = 10;
			
			indexFileName = "index.m3u8";
			
			segmentFilePrefix = "segment";
			
			prevBufPos = 0;
			
			createIndexFile();
		}
		
		private void createIndexFile() {
			
			String fullIndexFileName = outputDir + indexFileName;
						
			File oldsegment = new File(getSegmentFileName());
			
			if(oldsegment.exists()) {
				
				oldsegment.delete();
			}
			
			BufferedWriter out;
			
			try {
					
				out = new BufferedWriter(new FileWriter(fullIndexFileName, false)); 
				out.write("#EXTM3U");
				out.newLine();
				out.write("#EXT-X-TARGETDURATION:" + Integer.toString(segmentLengthSeconds));
				out.newLine();
				//out.write("#EXT-X-MEDIA-SEQUENCE:1");
				//out.newLine();
				out.write("#EXTINF:"+ Integer.toString(segmentLengthSeconds) + ",");
				out.newLine();
				out.write(publishURL + segmentFilePrefix + Integer.toString(segmentNr) + ".ts");
				out.newLine();
				out.close();				
				
			} catch (UnsupportedEncodingException e1) {
			
				e1.printStackTrace();
				
			} catch (FileNotFoundException e1) {
			
				e1.printStackTrace();
				
			} catch (IOException e) {
			
				e.printStackTrace();
			}
			
		}
		
		public void addSegmentToIndexFile(boolean isFinalSegment) {
			
			// first segment is already added when creating the index file
			if(segmentNr == 0) return;
			
			String fullIndexFileName = outputDir + indexFileName;
			
			BufferedWriter out;
			
			try {
				
				out = new BufferedWriter(new FileWriter(fullIndexFileName, true)); 
				out.write("#EXTINF:"+ Integer.toString(segmentLengthSeconds) + ",");
				out.newLine();
				out.write(publishURL + segmentFilePrefix + Integer.toString(segmentNr) + ".ts");
				out.newLine();
				
				if(isFinalSegment) {
					
					out.write("#EXT-X-ENDLIST");
				}
				
				out.close();				
				
			} catch (UnsupportedEncodingException e1) {
			
				e1.printStackTrace();
				
			} catch (FileNotFoundException e1) {
			
				e1.printStackTrace();
				
			} catch (IOException e) {
			
				e.printStackTrace();
			}
			
		}
		
		public String getSegmentFileName() {
			
			return(outputDir + segmentFilePrefix + Integer.toString(segmentNr) + ".ts");
			
		}
		
	}

	CoderInfo videoDecoder;
	CoderInfo audioDecoder;
	CoderInfo videoEncoder;
	CoderInfo audioEncoder;

	String inputVideoFile;
	String outputDir;
	String publishURL;

	boolean running;
	boolean segmentedOutput;
	
	OutputStream outputStream;
	RandomAccessFile outputFile;

	public Transcode(String threadName, String inputVideoFile, String outputDir, String publishURL) {

		super(threadName);
		
		this.outputDir = outputDir;
		this.inputVideoFile = inputVideoFile;
		this.publishURL = publishURL;

		outputStream = new ByteArrayOutputStream();

		videoDecoder = audioDecoder = videoEncoder = audioEncoder = null;		
		running = false;
		segmentedOutput = true;

	}

	public Transcode(String threadName, String inputVideoFile, PipedOutputStream pipedOutputStream) {
		
		super(threadName);
		
		this.inputVideoFile = inputVideoFile;
	
		outputStream = pipedOutputStream;

		videoDecoder = audioDecoder = videoEncoder = audioEncoder = null;		
		running = false;
		segmentedOutput = false;
		
	}
	
	public Transcode(String threadName, String inputVideoFile, String outputFileName) {
		
		super(threadName);
		
		this.inputVideoFile = inputVideoFile;
		
		try {
			
			outputFile = new RandomAccessFile(outputFileName, "rw");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		videoDecoder = audioDecoder = videoEncoder = audioEncoder = null;		
		running = false;
		segmentedOutput = false;
		
	}
	
	public void stopRunning() {
		
		running = false;
	}
	
	@Override
	public void run() {

		running = true;
		
		System.out.println("Transcoding: " + inputVideoFile + " to " + publishURL);

		IContainer decoderContainer = IContainer.make();

		// open the input video file
		int success = decoderContainer.open(inputVideoFile, IContainer.Type.READ, null, true, true);
		if(success < 0) {

			throw new RuntimeException("XUGGLER: could not open input file: " + inputVideoFile); 
		}

		// create a output container which will convert the input video
		IContainer encoderContainer = createEncoderContainer(decoderContainer);

		success = videoDecoder.coder.open();		
		if(success < 0) {

			throw new RuntimeException("XUGGLER: could not open video decoder for container: "
					+ inputVideoFile);
		}

		if(audioDecoder != null) {

			success = audioDecoder.coder.open();		
			if(success < 0) {

				throw new RuntimeException("XUGGLER: could not open audio decoder for container: "
						+ inputVideoFile);
			}
		}

		IVideoResampler videoResampler = null;

		if(videoDecoder.coder.getPixelType() != videoEncoder.coder.getPixelType() ||
				videoDecoder.coder.getHeight() != videoEncoder.coder.getHeight() ||
				videoDecoder.coder.getWidth() != videoEncoder.coder.getWidth())
		{

			videoResampler = IVideoResampler.make(videoEncoder.coder.getWidth(), 
					videoEncoder.coder.getHeight(), videoEncoder.coder.getPixelType(),
					videoDecoder.coder.getWidth(), videoDecoder.coder.getHeight(), 
					videoDecoder.coder.getPixelType());

			if(videoResampler == null) {

				throw new RuntimeException("XUGGLER: could not create video resampler for: " 
						+ inputVideoFile);
			}			
		}

		IAudioResampler audioResampler = null;

		if(audioDecoder != null && 
				(audioDecoder.coder.getChannels() != audioEncoder.coder.getChannels() ||
				audioDecoder.coder.getSampleRate() != audioEncoder.coder.getSampleRate() ||
				audioDecoder.coder.getSampleFormat() != audioEncoder.coder.getSampleFormat()))
		{

			audioResampler = IAudioResampler.make(audioEncoder.coder.getChannels(), 
					audioDecoder.coder.getChannels(), audioEncoder.coder.getSampleRate(), 
					audioDecoder.coder.getSampleRate(), audioEncoder.coder.getSampleFormat(),
					audioDecoder.coder.getSampleFormat());

			if(audioResampler == null) {

				throw new RuntimeException("XUGGLER: could not create audio resampler for: " 
						+ inputVideoFile);
			}			
		}

		/*
		 * We allocate a set of samples with the same number of channels as the
		 * coder tells us is in this buffer.
		 * 
		 * We also pass in a buffer size (1024 in our example), although Xuggler
		 * will probably allocate more space than just the 1024 (it's not important why).
		 */		

		IAudioSamples decodeSamples = null;
		IAudioSamples reSamples = null;

		if(audioDecoder != null) {

			decodeSamples = IAudioSamples.make(1024, audioDecoder.coder.getChannels());
			reSamples = IAudioSamples.make(1024, audioEncoder.coder.getChannels());			
		}

		/*
		 * We allocate a new picture to get the data out of Xuggler
		 */
		IVideoPicture decodePicture = IVideoPicture.make(videoDecoder.coder.getPixelType(),
				videoDecoder.coder.getWidth(), videoDecoder.coder.getHeight());

		IVideoPicture resampledPicture = IVideoPicture.make(videoEncoder.coder.getPixelType(),
				videoEncoder.coder.getWidth(), videoEncoder.coder.getHeight());

		IPacket decodePacket = IPacket.make();
		IPacket encodePacket = IPacket.make();

		SegmentInfo segmentInfo = null;		
		if(segmentedOutput) {
			
			segmentInfo = new SegmentInfo(outputDir, publishURL);
		}

		while(decoderContainer.readNextPacket(decodePacket) >= 0 && running == true)
		{
						
			/**
			 * Find out if this stream has a starting timestamp
			 */
			IStream stream = decoderContainer.getStream(decodePacket.getStreamIndex());
			long tsOffset = 0;
			if (stream.getStartTime() != Global.NO_PTS && stream.getStartTime() > 0 && stream.getTimeBase() != null)
			{
				IRational defTimeBase = IRational.make(1, (int)Global.DEFAULT_PTS_PER_SECOND);
				tsOffset = defTimeBase.rescale(stream.getStartTime(), stream.getTimeBase()); 
			}


			/*
			 * Now we have a packet, let's see if it belongs to our video stream
			 */
			if(decodePacket.getStreamIndex() == videoDecoder.streamIndex)
			{


				int offset = 0;
				while(offset < decodePacket.getSize())
				{
					/*
					 * Now, we decode the video, checking for any errors.
					 * 
					 */
					int bytesDecoded = videoDecoder.coder.decodeVideo(decodePicture, decodePacket, offset);
					if(bytesDecoded < 0) {

						throw new RuntimeException("XUGGLER: error decoding video in: " + inputVideoFile);
					}

					if(decodePicture.getTimeStamp() != Global.NO_PTS) {

						decodePicture.setTimeStamp(decodePicture.getTimeStamp() - tsOffset);
					}


					offset += bytesDecoded;
					/*
					 * Some decoders will consume data in a packet, but will not be able to construct
					 * a full video picture yet.  Therefore you should always check if you
					 * got a complete picture from the decoder
					 */
					if(decodePicture.isComplete())
					{
						IVideoPicture encodePicture = decodePicture;						
						/*
						 * If the resampler is not null, that means we didn't get the
						 * video in BGR24 format and
						 * need to convert it into BGR24 format.
						 */
						if(videoResampler != null)
						{

							if(videoResampler.resample(resampledPicture, decodePicture) < 0) {

								throw new RuntimeException("XUGGLER: could not resample video from: "
										+ inputVideoFile);
							}

							encodePicture = resampledPicture;

						}
						//if(newPic.getPixelType() != IPixelFormat.Type.BGR24)
						//	throw new RuntimeException("could not decode video" +
						//			" as BGR 24 bit data in: " + filename);
						
						videoEncoder.coder.encodeVideo(encodePacket, encodePicture, -1);	

						if(encodePacket.isComplete()) {

							if(segmentedOutput) {
								// 	split the file into equal segments of segmentLengthTime seconds 
								createSegment(encodePacket, segmentInfo, false);
							}
							
							encoderContainer.writePacket(encodePacket, true);
						}
					}

				}

			} else if(audioDecoder != null && decodePacket.getStreamIndex() == audioDecoder.streamIndex) {


				/*
				 * A packet can actually contain multiple sets of samples (or frames of samples
				 * in audio-decoding speak).  So, we may need to call decode audio multiple
				 * times at different offsets in the packet's data.  We capture that here.
				 */
				int offset = 0;

				/*
				 * Keep going until we've processed all data
				 */
				while(offset < decodePacket.getSize())
				{
					int bytesDecoded = audioDecoder.coder.decodeAudio(decodeSamples, decodePacket, offset);
					if(bytesDecoded < 0) {
						break;
						//throw new RuntimeException("XUGGLER: got error decoding audio in: " + inputVideoFile);

					}

					if(decodeSamples.getTimeStamp() != Global.NO_PTS) {

						decodeSamples.setTimeStamp(decodeSamples.getTimeStamp() - tsOffset);
					}


					offset += bytesDecoded;
					/*
					 * Some decoder will consume data in a packet, but will not be able to construct
					 * a full set of samples yet.  Therefore you should always check if you
					 * got a complete set of samples from the decoder
					 */
					if(decodeSamples.isComplete())
					{

						IAudioSamples encodeSamples = decodeSamples;

						if(audioResampler != null) {

							audioResampler.resample(reSamples, decodeSamples, decodeSamples.getNumSamples());
							encodeSamples = reSamples;
						}

						int numSamplesConsumed = 0;
						
						while(numSamplesConsumed < encodeSamples.getNumSamples()) {

							int samplesConsumed = audioEncoder.coder.encodeAudio(encodePacket, encodeSamples, numSamplesConsumed);

							if(samplesConsumed  < 0) {

								throw new RuntimeException("XUGGLER: got error encoding audio in: " + inputVideoFile);
							}

							numSamplesConsumed += samplesConsumed;

							if(encodePacket.isComplete()) {

								if(segmentedOutput) {
									// 	split the file into equal segments of segmentLengthTime seconds 
									createSegment(encodePacket, segmentInfo, false);
								}
								
								encoderContainer.writePacket(encodePacket, true);
							}
						}
					}
				}

			} else {

				/*
				 * This packet isn't part of our video stream, so we just
				 * silently drop it.
				 */			
				continue;

			}
			
	
		}

		if(segmentedOutput) {
			
			createSegment(encodePacket, segmentInfo, true);
		}
		
		closeStreams(decoderContainer, encoderContainer);

	}
	
	private void createSegment(IPacket encodePacket, SegmentInfo segmentInfo, boolean isFinalSegment) {

		double segmentTime = 0;

		if(encodePacket.getStreamIndex() == videoEncoder.streamIndex && encodePacket.isKey())
		{
			segmentTime = encodePacket.getPts() * (double)encodePacket.getTimeBase().getNumerator() / encodePacket.getTimeBase().getDenominator();
		}
		else if(audioEncoder != null && encodePacket.getStreamIndex() == audioEncoder.streamIndex)
		{
			segmentTime = encodePacket.getPts() * (double)encodePacket.getTimeBase().getNumerator() / encodePacket.getTimeBase().getDenominator();
		}
		else
		{
			segmentTime = segmentInfo.prevSegmentTime;
		}

		// done writing the current file?
		if(segmentTime - segmentInfo.prevSegmentTime >= segmentInfo.segmentLengthSeconds || isFinalSegment)
		{			

			FileOutputStream segment;
			
			ByteArrayOutputStream byteArrayOutputStream = 
					(ByteArrayOutputStream) outputStream;
			
			
			try {
				
				segment = new FileOutputStream(segmentInfo.getSegmentFileName());
								
				byteArrayOutputStream.writeTo(segment);
				
				//byte[] data = byteArrayOutputStream.toByteArray();				
				//segment.write(data,segmentInfo.prevBufPos, data.length - segmentInfo.prevBufPos);
				
				//segment.write(byteArrayOutputStream.toByteArray());
				
				segment.close();	
				byteArrayOutputStream.reset();
				
				segmentInfo.addSegmentToIndexFile(isFinalSegment);
				
				System.out.println("Finished segment " + Integer.toString(segmentInfo.segmentNr) +
						" at :" + Double.toString(segmentTime) + " seconds.");
				
				segmentInfo.segmentNr++;
				
				segmentInfo.prevSegmentTime = segmentTime;
				segmentInfo.prevBufPos = byteArrayOutputStream.size();								
				
			} catch (FileNotFoundException e) {
	
				e.printStackTrace();
				
			} catch (IOException e) {

				e.printStackTrace();
			} 
									
		}
	}

	private void closeStreams(IContainer decoderContainer, IContainer encoderContainer) {

		/**
		 * Some video coders (e.g. MP3) will often "read-ahead" in a stream and keep
		 * extra data around to get efficient compression.  But they need some way to know
		 * they're never going to get more data.  The convention for that case is to pass
		 * null for the IMediaData (e.g. IAudioSamples or IVideoPicture) in encodeAudio(...)
		 * or encodeVideo(...) once before closing the coder.
		 * 
		 * In that case, the IStreamCoder will flush all data.
		 */
		IPacket nullPacket = IPacket.make();

		videoEncoder.coder.encodeVideo(nullPacket, null, 0);

		if(audioEncoder != null) {

			audioEncoder.coder.encodeAudio(nullPacket, null, 0);

		}

		/**
		 * Some container formats require a trailer to be written to avoid a corrupt files.
		 * 
		 * Others, such as the FLV container muxer, will take a writeTrailer() call to tell
		 * it to seek() back to the start of the output file and write the (now known) duration
		 * into the Meta Data.
		 * 
		 *  So trailers are required.  In general if a format is a streaming format, then the
		 *  writeTrailer() will never seek backwards.
		 *  
		 *  Make sure you don't close your codecs before you write your trailer, or
		 *  we'll complain loudly and not actually write a trailer.
		 */
		int success = encoderContainer.writeTrailer();
		if(success < 0) {

			throw new RuntimeException("XUGGLER: Could not write trailer to output file");
		}

		/**
		 * We do a nice clean-up here to show you how you should do it.
		 * 
		 * That said, Xuggler goes to great pains to clean up after you if you
		 * forget to release things.  But still, you should be a good boy or giral and
		 * clean up yourself.
		 */
		videoDecoder.coder.close();
		if(audioDecoder != null) {

			audioDecoder.coder.close();
		}

		videoEncoder.coder.close();
		if(audioEncoder != null) {

			audioEncoder.coder.close();
		}
		/**
		 * Tell Xuggler it can close the output file, write all data, and free all relevant memory.
		 */
		decoderContainer.close();
		/**
		 * And do the same with the input file.
		 */
		encoderContainer.close();

		try {
			
			if(outputStream != null) {
				
				outputStream.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
	}


	private IContainer createEncoderContainer(
			IContainer decodeContainer) 
	{

		IContainer encoderContainer = IContainer.make();

		IContainerFormat format = IContainerFormat.make();
		format.setOutputFormat("mpegts", null, null);

		int success; 
				
		if(outputStream != null) {
			
			success = encoderContainer.open(outputStream, format);
		
		} else {
			
			success = encoderContainer.open(outputFile, IContainer.Type.WRITE, format);
		}
		
		
		//int success = encoderContainer.open(outputFile, IContainer.Type.WRITE, format);
		//int success = encoderContainer.open(outputVideoFile, IContainer.Type.WRITE, null);		
		if(success < 0) {

			throw new RuntimeException("XUGGLER: could not open output stream"); 
		}
				
		for(int streamIndex = 0; streamIndex < decodeContainer.getNumStreams(); streamIndex++) {

			IStreamCoder coder = decodeContainer.getStream(streamIndex).getStreamCoder();

			switch(coder.getCodecType()) {

			case CODEC_TYPE_VIDEO: {
				videoDecoder = new CoderInfo(coder, streamIndex);
				break;
			}			
			case CODEC_TYPE_AUDIO: {

				audioDecoder = new CoderInfo(coder, streamIndex);
				break;
			}
			default: {

				break;
			}

			}

		}

		if(videoDecoder != null) {

			addVideoEncoder(encoderContainer);
		}

		if(audioDecoder != null) {

			addAudioEncoder(encoderContainer);
		}

		success = encoderContainer.writeHeader(); 		
		if(success < 0) {

			throw new RuntimeException("XUGGLER: error writing header"); 
		}

		return encoderContainer;
	}

	void addVideoEncoder(IContainer encoderContainer) {

		// setup video coder

		IStream videoStream = encoderContainer.addNewStream(encoderContainer.getNumStreams());
		videoEncoder = new CoderInfo(videoStream.getStreamCoder(), encoderContainer.getNumStreams() - 1);

		ICodec videoCodec = ICodec.findEncodingCodecByName("libx264");//guessEncodingCodec(null, "mpegts", "video/MP2T", null, ICodec.Type.CODEC_TYPE_VIDEO); 

		videoEncoder.coder.getDirection();
		videoEncoder.coder.setCodec(videoCodec);

		//videoEncoder.setBitRate(614400);
		//videoEncoder.setBitRateTolerance(614400);
		videoEncoder.coder.setPixelType(IPixelFormat.Type.YUV420P);
		videoEncoder.coder.setHeight(300);
		videoEncoder.coder.setWidth(480);
		//videoEncoder.coder.setBitRate(800);

		//IRational fps = videoDecoder.coder.getFrameRate();

		videoEncoder.coder.setFrameRate(IRational.make(1, 30));
		videoEncoder.coder.setTimeBase(IRational.make(1, 30));

		//videoEncoder.coder.setFrameRate(fps);
		//videoEncoder.coder.setTimeBase(IRational.make(fps.getDenominator(), fps.getNumerator()));

		//videoEncoder.coder.setBitRate(videoDecoder.coder.getBitRate());
		//videoEncoder.coder.setBitRateTolerance(videoDecoder.coder.getBitRateTolerance());
		//videoEncoder.coder.setTimeBase(videoDecoder.coder.getTimeBase());

		//outStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
		//outStreamCoder.setGlobalQuality(0); 

		videoEncoder.coder.setProperty("f", "mpegts");
		videoEncoder.coder.setProperty("er", 4);
		videoEncoder.coder.setProperty("b", "600k");
		videoEncoder.coder.setProperty("flags", "+loop+mv4");
		videoEncoder.coder.setProperty("cmp", "256");
		videoEncoder.coder.setProperty("partitions", "+parti4x4+partp8x8+partb8x8");
		videoEncoder.coder.setProperty("subq", 7);
		videoEncoder.coder.setProperty("trellis", 1);
		videoEncoder.coder.setProperty("refs", 5);
		videoEncoder.coder.setProperty("coder", 0);
		videoEncoder.coder.setProperty("me_range", 16);
		videoEncoder.coder.setProperty("keyint_min", 25);
		videoEncoder.coder.setProperty("sc_threshold", 40);
		videoEncoder.coder.setProperty("i_qfactor", 0.71);
		videoEncoder.coder.setProperty("bt", "600k");
		videoEncoder.coder.setProperty("maxrate", "600k");
		videoEncoder.coder.setProperty("qcomp", 0.6);
		videoEncoder.coder.setProperty("qmin", 10);
		videoEncoder.coder.setProperty("qmax", 51);
		videoEncoder.coder.setProperty("qdiff", 4);
		videoEncoder.coder.setProperty("level", 30);
		videoEncoder.coder.setProperty("rc_eq", "blurCplx^(1-qComp)");
		videoEncoder.coder.setProperty("r", 30);
		videoEncoder.coder.setProperty("g", 90);
		videoEncoder.coder.setProperty("async", 2);

/*		
		videoEncoder.coder.setProperty("f", "mpegts");
		videoEncoder.coder.setProperty("acodec", "libmp3lame");
		videoEncoder.coder.setProperty("ar", 48000);
		videoEncoder.coder.setProperty("ab", "64k");
		videoEncoder.coder.setProperty("vcodec", "libx264");
		videoEncoder.coder.setProperty("b", "800k");		
		videoEncoder.coder.setProperty("er", 4);		
		videoEncoder.coder.setProperty("flags", "+loop");
		videoEncoder.coder.setProperty("cmp", "+chroma");
		videoEncoder.coder.setProperty("partitions", "+parti4x4+partp8x8+partb8x8");
		videoEncoder.coder.setProperty("subq", 5);
		videoEncoder.coder.setProperty("trellis", 1);
		videoEncoder.coder.setProperty("refs", 1);
		videoEncoder.coder.setProperty("coder", 0);
		videoEncoder.coder.setProperty("me_range", 16);
		videoEncoder.coder.setProperty("keyint_min", 25);
		videoEncoder.coder.setProperty("sc_threshold", 40);
		videoEncoder.coder.setProperty("i_qfactor", 0.71);
		videoEncoder.coder.setProperty("bt", "200k");
		videoEncoder.coder.setProperty("maxrate", "800k");
		videoEncoder.coder.setProperty("bufsize", "800k");
		videoEncoder.coder.setProperty("rc_eq", "blurCplx^(1-qComp)");
		videoEncoder.coder.setProperty("qcomp", 0.6);
		videoEncoder.coder.setProperty("qmin", 10);
		videoEncoder.coder.setProperty("qmax", 51);
		videoEncoder.coder.setProperty("qdiff", 4);
		videoEncoder.coder.setProperty("level", 30);		
		videoEncoder.coder.setProperty("g", 30);
		videoEncoder.coder.setProperty("async", 2);
*/		
		int success = videoEncoder.coder.open();		
		if(success < 0) {

			throw new RuntimeException("XUGGLER: video coder error"); 
		}


	}

	private void addAudioEncoder(IContainer encoderContainer) {

		// setup audio coder

		IStream audioStream = encoderContainer.addNewStream(encoderContainer.getNumStreams());
		audioEncoder = new CoderInfo(audioStream.getStreamCoder(), encoderContainer.getNumStreams() - 1);

		ICodec audioCodec = ICodec.findEncodingCodecByName("libmp3lame");

		audioEncoder.coder.setCodec(audioCodec);

		audioEncoder.coder.setSampleRate(audioDecoder.coder.getSampleRate());
		audioEncoder.coder.setBitRate(audioDecoder.coder.getBitRate());
		audioEncoder.coder.setChannels(audioDecoder.coder.getChannels());
		//audioEncoder.coder.setTimeBase(IRational.make(1, 30));
		//audioEncoder.coder.setFrameRate(IRational.make(1, 30));

		int success = audioEncoder.coder.open();		
		if(success < 0) {

			throw new RuntimeException("XUGGLER: audio coder error"); 
		}

	}

	/*
	private IContainer createWriter() {

		IStreamCoder mOutAudioCoder;
		IStreamCoder mOutVideoCoder; 

		String outputURL =  "rtmp://localhost:1936/live2/abcd2";

		ISimpleMediaFile outputInfo = new SimpleMediaFile();
		outputInfo.setHasAudio(false);
		outputInfo.setAudioBitRate(32000);
		outputInfo.setAudioChannels(1);
		outputInfo.setAudioSampleRate(22050);
		outputInfo.setAudioCodec(ICodec.ID.CODEC_ID_AAC);
		outputInfo.setHasVideo(true);
		// Unfortunately the Trans-coder needs to know the width and height
		// you want to output as; even if you don't know yet.
		outputInfo.setVideoWidth(320);
		outputInfo.setVideoHeight(240);
		outputInfo.setVideoBitRate(200000);
		outputInfo.setVideoCodec(ICodec.ID.CODEC_ID_H264); 

		IContainer writeContainer = IContainer.make();

		IContainerFormat outFormat = IContainerFormat.make();
		outFormat.setOutputFormat("flv", outputURL, null);		
		int retval = writeContainer.open(outputURL, IContainer.Type.WRITE, outFormat);
		if(retval < 0) {	 

			throw new RuntimeException("XUGGLER: could not open output: " 
					+ outputURL);
		}

		if(outputInfo.hasAudio())
		{
			// Add an audio stream
			IStream outStream = writeContainer.addNewStream(0);
			if(outStream == null) {

				throw new RuntimeException("XUGGLER: could not add audio stream to output: " 
						+ outputURL);
			}

			IStreamCoder outCoder = outStream.getStreamCoder();
			ICodec.ID outCodecId = outputInfo.getAudioCodec();
			ICodec outCodec = ICodec.findEncodingCodec(outCodecId);          
			if(outCodec == null)
			{
				throw new RuntimeException("XUGGLER: Could not encode using the codec:" 
						+ outputInfo.getAudioCodec());
			}

			outCoder.setCodec(outCodec);
			outCoder.setBitRate(outputInfo.getAudioBitRate());

			outCoder.setSampleRate(outputInfo.getAudioSampleRate());

			outCoder.setChannels(outputInfo.getAudioChannels());

			outCoder.open();
			// if we get here w/o an exception, record the coder
			mOutAudioCoder = outCoder;
		}

		if(outputInfo.hasVideo())
		{
			// Add an audio stream
			IStream outStream = writeContainer.addNewStream(1);
			if(outStream == null) {

				throw new RuntimeException("XUGGLER: could not add video stream to output: " 
				+ outputURL);
			}

			IStreamCoder outCoder = outStream.getStreamCoder();
			ICodec.ID outCodecId = outputInfo.getVideoCodec();
			ICodec outCodec = ICodec.findEncodingCodec(outCodecId);
			if(outCodec == null)
			{
				throw new RuntimeException("XUGGLER: Could not encode using the codec: " 
				+ outputInfo.getAudioCodec());
			}

			outCoder.setCodec(outCodec);
			outCoder.setWidth(outputInfo.getVideoWidth());
			outCoder.setHeight(outputInfo.getVideoHeight());
			outCoder.setPixelType(outputInfo.getVideoPixelFormat());
			outCoder.setGlobalQuality(outputInfo.getVideoGlobalQuality());
			outCoder.setBitRate(outputInfo.getVideoBitRate());
			outCoder.setNumPicturesInGroupOfPictures(10);
			outCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);

			outCoder.setTimeBase(IRational.make(1, 25)); // default to FLV

			outCoder.setProperty("b", "96k");
			outCoder.setProperty("flags", "+loop");
			outCoder.setProperty("cmp", "+chroma");
			outCoder.setProperty("partitions", "+parti4x4+partp8x8+partb8x8");
			outCoder.setProperty("subq", 6);
			outCoder.setProperty("trellis", 1);
			outCoder.setProperty("refs", 1);
			outCoder.setProperty("coder", 0);
			outCoder.setProperty("me_range", 20);
			outCoder.setProperty("keyint_min", 25);
			outCoder.setProperty("sc_threshold", 20);
			outCoder.setProperty("i_qfactor", 0.71);
			outCoder.setProperty("bt", "200k");
			outCoder.setProperty("maxrate", "96k");
			outCoder.setProperty("qcomp", 0.6);
			outCoder.setProperty("qmin", 10);
			outCoder.setProperty("qmax", 51);
			outCoder.setProperty("qdiff", 4);
			outCoder.setProperty("level", 30);
			outCoder.setProperty("ar", true);
			outCoder.setProperty("re", true);
			outCoder.setProperty("g", 250);
			outCoder.setProperty("fps", 25);

			outCoder.open();
			// if we get here w/o an exception, record the coder
			mOutVideoCoder = outCoder;
		}

		retval = writeContainer.writeHeader();
		if (retval < 0)
		{
			throw new RuntimeException("XUGGLER: could not write header for output");
		} 
	}


	public void someFunc(String inputFile, String outputFile) throws IOException {

		IContainer readContainer = IContainer.make();

		ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(byte_out);

		//read a rtmp stream
		readContainer.open(inputFile,IContainer.Type.READ, null, true, false);
		IPacket packet = IPacket.make();

		//get a write container which writes into DataOutputStream os
		IContainer writeContainer = createWriter(os, readContainer);

		//set a break flag
		boolean breakFlag = false;

		//fetch packets and write them to DataOutputStream os using the
		//writer got in previous step
		while(readContainer.readNextPacket(packet) >= 0 &&
				packet.isComplete() && !breakFlag) 
		{
			if (byte_out.size() < 10000000) {
				writeContainer.writePacket(packet);
				System.out.println(byte_out.size());
			} else {
				//if data read from rtmp stream is approx 20000000 bytes then dump
				//the data from DataOutputStream os into a byte array defined in the
				//next command
				byte[] ba = new byte[20000000];
				ba = byte_out.toByteArray();
				System.out.println("bytes read into byte array = " + ba.length);
				System.out.println("dumping byte array into a flv file - (bafile.flv)");
				//after getting the data into a byte array save this byte array into
				//COSAR or for now simply write the byte array into a file names
				//bafile.flv
				FileOutputStream fos = new FileOutputStream(outputFile);
				fos.write(ba);
				//wrap up by breaking out of the loop
				breakFlag=true;
			}
		}
		//free the container of its job after giving it the peanut
		//(writeTrailer)
		writeContainer.writeTrailer();
		writeContainer.close();

		//leave the stream alone...
		readContainer.close();


	}

	private static IContainer createWriter(DataOutputStream name, IContainer readContainer) {

		IContainer writeContainer = IContainer.make();

		//function does all the crazy stuff of getting a xuggler IContainer writer
		IContainerFormat format = IContainerFormat.make();
		format.setOutputFormat("flv", null, null);
		writeContainer.open(name, format);

		IStream inVideoStream = readContainer.getStream(0);
		IStreamCoder inVideocoder = inVideoStream.getStreamCoder();

		inVideocoder.setCodec(ICodec.ID.CODEC_ID_H264);
		inVideocoder.setTimeBase(inVideoStream.getTimeBase());
		inVideocoder.setHeight(320);
		inVideocoder.setWidth(240);

		IStream outVideoStream = writeContainer.addNewStream(0);
		outVideoStream.setStreamCoder(inVideocoder);

		int video = inVideocoder.open();

		if (video >= 0) {
			System.out.println("Good videoStream");
		} else {
			System.out.println("Wrong videoStream");
		}

		IStream inAudioStream = readContainer.getStream(1);
		IStreamCoder inAudioCoder = inAudioStream.getStreamCoder();

		inAudioCoder.setCodec(ICodec.ID.CODEC_ID_MP3);
		inAudioCoder.setSampleRate(44100);

		inAudioCoder.setTimeBase(inAudioStream.getTimeBase());

		IStream outAudioStream = writeContainer.addNewStream(1);
		outAudioStream.setStreamCoder(inAudioCoder);

		int audio = inAudioCoder.open();
		if (audio >= 0) {
			System.out.println("Good audioStream");
		} else {
			System.out.println("Wrong audioStream");
		}
		int header = writeContainer.writeHeader();

		if (header == 0) {
			System.out.println("good header");
		} else {
			System.out.println("wrong header" + header);
		}

		return writeContainer;
	}
	 */

} 



