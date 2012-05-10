package timer;

public class StopWatch {

	long start;
	long stop;
	
	public StopWatch() {
		
		start = 0;
		stop = 0;
	}
	
	
	public void start() {
		start = System.currentTimeMillis();
	}

	public void stop() {
		
		stop = System.currentTimeMillis();
	}
	
	public long timeMiliSeconds() {
		
		return(stop - start);
	}
	
	public long timeSeconds() {
		
		return((stop - start) / 1000);
	}
	
	public double timeMinutes() {
	
		return(timeSeconds() / 60.0);
		
	}
}

