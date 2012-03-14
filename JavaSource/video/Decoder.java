package video;

import virtualFile.VirtualInputFile;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

public class Decoder {

	protected VirtualInputFile input;
	
	protected IContainer container;
	protected IContainerFormat format;
	
	protected IStreamCoder videoStream;	
	protected int videoStreamIndex;
	protected IStreamCoder audioStream;
	protected int audioStreamIndex;
	
	protected IPacket packet;
	
	enum DecodeMode {
		NORMAL,
		STOP,
		IGNORE_AUDIO
	}
	
	
	protected DecodeMode decodeMode;
	
	public Decoder(VirtualInputFile input) {
			
		videoStream = null;
		audioStream = null;
		container = null;
		format = null;
				
		open(input);	
	}
	
	public void open(VirtualInputFile input) {
	
		close();
		
		this.input = input;
		
		container = IContainer.make();
		format = IContainerFormat.make();
		packet = IPacket.make();

		// open the input video file
		int success = container.open(input, format, true, true);
/*		int success = -1;
		
		try {
			RandomAccessFile f = new RandomAccessFile("j:/grey.avi", "r");
			success = container.open(f, Type.READ, format);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		
		if(success < 0) {

			throw new RuntimeException("XUGGLER DECODER: could not open input: " + input.getLocation().getLocation()); 
		}

		for(int streamIndex = 0; streamIndex < container.getNumStreams(); streamIndex++) {

			IStreamCoder coder = container.getStream(streamIndex).getStreamCoder();

			switch(coder.getCodecType()) {

				case CODEC_TYPE_VIDEO: {
					videoStream = coder;
					videoStreamIndex = streamIndex;
					break;
				}			
				case CODEC_TYPE_AUDIO: {
	
					audioStream = coder;
					audioStreamIndex = streamIndex;
					break;
				}
				default: {
	
					break;
				}

			}

		}
		
		setDecodeMode(DecodeMode.NORMAL);
	}

	public IContainer getContainer() {		
		return container;
	}
	
	public IStreamCoder getVideoStream() {
		return videoStream;
	}

	public IStreamCoder getAudioStream() {
		return audioStream;
	}

	public IContainerFormat getFormat() {
		
		return(format);
	}
	
	public void close() {
	
		if(videoStream != null) {
			
			videoStream.close();
		}
		
		if(audioStream != null) {

			audioStream.close();
		}

		if(container != null) {
					
			container.close();
		}
		
		videoStreamIndex = -1;
		audioStreamIndex = -1;
	
	}

	public int getVideoStreamIndex() {
		return videoStreamIndex;
	}

	public int getAudioStreamIndex() {
		return audioStreamIndex;
	}

	public void decode() {
		
		int success = videoStream.open();		
		if(success < 0) {

			throw new RuntimeException("XUGGLER DECODER: could not open video decoder for container: "
					+ input.getLocation().getLocation());
		}
				
		IAudioSamples decodeSamples = null;
	
		if(audioStream != null) {

			success = audioStream.open();		
			if(success < 0) {

				throw new RuntimeException("XUGGLER DECODER: could not open audio decoder for container: "
						+ input.getLocation().getLocation());
			}
			
			decodeSamples = IAudioSamples.make(1024, audioStream.getChannels());			
		}
		
		
		
		IVideoPicture decodePicture = IVideoPicture.make(videoStream.getPixelType(),
				videoStream.getWidth(), videoStream.getHeight());
		
	
		while(container.readNextPacket(packet) >= 0 && decodeMode != DecodeMode.STOP)
		{
										
			/**
			 * Find out if this stream has a starting timestamp
			 */
			IStream stream = container.getStream(packet.getStreamIndex());
			long tsOffset = 0;
			if (stream.getStartTime() != Global.NO_PTS && stream.getStartTime() > 0 && stream.getTimeBase() != null)
			{
				IRational defTimeBase = IRational.make(1, (int)Global.DEFAULT_PTS_PER_SECOND);
				tsOffset = defTimeBase.rescale(stream.getStartTime(), stream.getTimeBase()); 
			}


			/*
			 * Now we have a packet, let's see if it belongs to our video stream
			 */
			if(packet.getStreamIndex() == videoStreamIndex)
			{

				int offset = 0;
				while(offset < packet.getSize())
				{
					/*
					 * Now, we decode the video, checking for any errors.
					 * 
					 */
					int bytesDecoded = videoStream.decodeVideo(decodePicture, packet, offset);
					if(bytesDecoded < 0) {

						throw new RuntimeException("XUGGLER: error decoding video in: " + input.getLocation().getLocation());
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
						
						decodedPicture(decodePicture);
						
					}

				}

			} else if(audioStream != null && packet.getStreamIndex() == audioStreamIndex
					&& decodeMode != DecodeMode.IGNORE_AUDIO) {


				/*
				 * A packet can actually contain multiple sets of samples (or frames of samples
				 * in audio-decoding speak).  So, we may need to call decode audio multiple
				 * times at different offsets in the packet's data.  We capture that here.
				 */
				int offset = 0;

				/*
				 * Keep going until we've processed all data
				 */
				while(offset < packet.getSize())
				{
					int bytesDecoded = audioStream.decodeAudio(decodeSamples, packet, offset);
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

						decodedAudioSamples(decodeSamples);
					
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
		
	}
	
	public void decodedPicture(IVideoPicture picture) {
		
		
	}
	
	public void decodedAudioSamples(IAudioSamples audioSamples) {
		
	}
	
	protected void setDecodeMode(DecodeMode mode) {
	
		decodeMode = mode;
	}
}
