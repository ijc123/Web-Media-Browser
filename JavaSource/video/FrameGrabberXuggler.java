package video;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.ImageUtil;
import virtualFile.Location;
import virtualFile.VirtualInputFile;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import database.ImageItem;

public class FrameGrabberXuggler extends Decoder {

	private Location location;
	private int curFrame;
	private int nrFrames;
	private String outputDir;	
	//private long timeStep;

	ImageUtil imageUtil;
	
	IConverter pictureConverter;
	
	public FrameGrabberXuggler(VirtualInputFile input, String outputDir, int nrFrames) {
		
		super(input);
				
		this.nrFrames = nrFrames;
		this.outputDir = outputDir;
		
		imageUtil = new ImageUtil();
					
		
	}
	
	@Override
	public void open(VirtualInputFile input) {
		
		super.open(input);
		
		pictureConverter =
				  ConverterFactory.createConverter(ConverterFactory.XUGGLER_BGR_24,
						  videoStream.getPixelType(), videoStream.getWidth(), videoStream.getHeight());
			
		curFrame = 0;
		
		location = input.getLocation();
			
		setDecodeMode(DecodeMode.IGNORE_AUDIO);
	}
	
	@Override
	public void decodedPicture(IVideoPicture picture) {
	
		if(curFrame == nrFrames) {
			
			setDecodeMode(DecodeMode.STOP);
			return;
		}
		
		BufferedImage image = pictureConverter.toImage(picture);
		
		ImageItem thumbNail = null;
		
		try {
			
			thumbNail = imageUtil.createThumbNail(image, 100);
			
			String thumbNr = Integer.toString(curFrame);
			
			String outputLocation = 
					outputDir + location.getFilenameWithoutExtension() + 
					"_" + thumbNr + ".jpg";
			
			FileOutputStream output = new FileOutputStream(outputLocation);
			
			output.write(thumbNail.getImageData());
			
			output.close();
			
			curFrame++;
			
			/*	
				long duration = container.getDuration();
				long timeStep = duration / nrFrames;
			*/
			
			long duration = input.length();
			long timeStep = duration / nrFrames;
			
			long curTimeStamp = picture.getTimeStamp();
			//long curTimeStamp = curFrame * timeStep;//packet.getPosition();
			long nextTimeStamp = curTimeStamp + timeStep; 
			
			long minTimeStamp = nextTimeStamp - timeStep / 10;
			long maxTimeStamp = nextTimeStamp + timeStep / 10;
		
			int success = container.seekKeyFrame(videoStreamIndex, 
					minTimeStamp, 
					nextTimeStamp, 
					maxTimeStamp, 
					IContainer.SEEK_FLAG_BYTE);
			
			if(success < 0) {
				
				throw new RuntimeException("FRAME GRABBER: " + IError.errorNumberToType(success) + " " + input.getLocation().getLocation()); 
			}
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
	}
	
	public int getNrFrames() {
		return nrFrames;
	}

	public void setNrFrames(int nrFrames) {
		this.nrFrames = nrFrames;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
}
