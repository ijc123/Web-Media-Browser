package video;


public class MediaPlayerBarrier {

	public enum Event {
		READY_TO_CONSUME_EVENT,
		ERROR,
		POSITION_CHANGE_DONE,
		SNAPSHOT_DONE
	}
	
	Event event;
	
	public MediaPlayerBarrier() {
		
		event = Event.READY_TO_CONSUME_EVENT;
	}
	
	synchronized Event waitForEvent(Event event) {
					
		while(this.event != event && this.event != Event.ERROR) {
		
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		// make sure we don't miss a error event
		event = this.event;
		// we handled the current event, signal a waiting producer
		// that the next event is ready to be consumed
		this.event = Event.READY_TO_CONSUME_EVENT;		
		notify();
		
		return(event);
	}
	
	synchronized void eventDone(Event event) {
					
		while(this.event != Event.READY_TO_CONSUME_EVENT) {
			
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		// set the completed event and notify a waiting consumer
		this.event = event; 		
		notify();
			
	}
}
