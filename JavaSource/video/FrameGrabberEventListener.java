package video;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class FrameGrabberEventListener extends MediaPlayerEventAdapter {

	private MediaPlayerBarrier barrier;
	private int nrFrames;
	private int curFrame;

	public FrameGrabberEventListener(MediaPlayerBarrier barrier, int nrFrames) {

		this.barrier = barrier;
		curFrame = 0;
		this.nrFrames = nrFrames;
	}

	@Override
	public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {

		float snapshotPos = curFrame / (float)nrFrames;

		if (newPosition >= snapshotPos * 0.9f) { /* 90% margin */

			curFrame++;
			barrier.eventDone(MediaPlayerBarrier.Event.POSITION_CHANGE_DONE);						
		}
		
	}

	@Override
	public void error(MediaPlayer mediaPlayer) {
		
		System.out.println("ERROR in media while grabbing frames");
		barrier.eventDone(MediaPlayerBarrier.Event.ERROR);
		
	}
	
	@Override
	public void finished(MediaPlayer mediaPlayer) {
		
		System.out.println("ERROR media end reached while grabbing frames");
		barrier.eventDone(MediaPlayerBarrier.Event.ERROR);
	}
	
	@Override
	public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
		System.out.println("snapshotTaken(filename=" + filename + ")");
		barrier.eventDone(MediaPlayerBarrier.Event.SNAPSHOT_DONE);		
	}
}