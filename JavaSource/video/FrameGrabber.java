package video;

import java.io.File;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import virtualFile.Location;
import virtualFile.LocationFactory;

/**
 * A simple application to generate a single thumbnail image from a media file.
 * <p>
 * This application shows how to implement the "vlc-thumb" sample (written in
 * "C") that is part of the vlc code-base.
 * <p>
 * The original "C" implementation is available in the vlc code-base here:
 * 
 * <pre>
 *   /vlc/doc/libvlc/vlc-thumb.c
 * </pre>
 * 
 * This implementation tries to stick as closely to the original "C"
 * implementation as possible, but uses a different synchronisation technique.
 * <p>
 * Since this is a test, the implementation is not very tolerant to errors.
 */
public class FrameGrabber {

	private static final String[] VLC_ARGS = { "--intf", "dummy", /* no interface */
	"--vout", "dummy", /* we don't want video (output) */
	"--no-audio", /* we don't want audio (decoding) */
	"--no-video-title-show", /* nor the filename displayed */
	"--no-stats", /* no stats */
	"--no-sub-autodetect-file", /* we don't want subtitles */
	"--no-disable-screensaver", /* we don't want interfaces */
	"--no-snapshot-preview", /* no blending in dummy vout */
	};


	public void start(String mrl, int imageWidth, String outputFileName, int nrFrames) throws Exception {

		Location output = LocationFactory.create(outputFileName);
		
		MediaPlayerFactory factory = new MediaPlayerFactory(VLC_ARGS);
		MediaPlayer mediaPlayer = factory.newHeadlessMediaPlayer();

		MediaPlayerBarrier barrier = new MediaPlayerBarrier();
		
		FrameGrabberEventListener e = new FrameGrabberEventListener(barrier, nrFrames);

		mediaPlayer.addMediaPlayerEventListener(e);
		
		if(mediaPlayer.startMedia(mrl)) {

			for(int i = 0; i < nrFrames; i++) {
							
				float snapShotPos = i / (float)nrFrames;
				
				mediaPlayer.setPosition(snapShotPos);
				MediaPlayerBarrier.Event event = barrier.waitForEvent(MediaPlayerBarrier.Event.POSITION_CHANGE_DONE); 
				
				if(event == MediaPlayerBarrier.Event.ERROR) break;
				
				String frameName = output.getLocationWithoutFilename() +
						output.getFilenameWithoutExtension() + Integer.toString(i) + ".png";
				
				File snapshotFile = new File(frameName);
				
				mediaPlayer.saveSnapshot(snapshotFile, imageWidth, 0);
				event = barrier.waitForEvent(MediaPlayerBarrier.Event.SNAPSHOT_DONE); 
				
				if(event == MediaPlayerBarrier.Event.ERROR) break;
			}

			mediaPlayer.stop();
		}

		

		mediaPlayer.release();
		factory.release();
	}
}
