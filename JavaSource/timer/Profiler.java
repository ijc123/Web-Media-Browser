package timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import debug.Log;

public class Profiler {
	
	class Counter {
		
		public String name;
		
		public int count;
		public long timePassedMilliSeconds;
		public StopWatch stopWatch;
		
		public Counter(String name) {
			
			this.name = name;
			
			count = 0;
			timePassedMilliSeconds = 0; 
			stopWatch = new StopWatch();
		}
	}
	
	class Result {
		
		public String name;
		public ArrayList<Counter> thread;
		public ArrayList<Long> threadId;
		
		int totalCount;
		double avgTimeMilliSeconds;
		int nrCounters;
		
		public Result() {
			
			name = "";
			
			totalCount = 0;
			avgTimeMilliSeconds = 0;
			nrCounters = 0;
			
			thread = new ArrayList<Counter>();
			threadId = new ArrayList<Long>();
		}
	}
	
	private ArrayList<Result> resultList;
	
	private ConcurrentHashMap<Long, Map<String, Counter>> map;
	
	public Profiler() {
		
		map = new ConcurrentHashMap<Long, Map<String, Counter>>();
		resultList = new ArrayList<Result>();
		
	}
	
	public void startCounter(long threadId, String name) {
		
		//Log.info(this, "starting counter: " + name + " id: " + threadId);
		
		Map<String, Counter> counters = map.get(threadId);
		
		if(counters == null) {
			
			counters = new HashMap<String, Counter>();
			map.put(threadId, counters);
		}
		
		Counter currentCounter = null;
		
		if((currentCounter = counters.get(name)) == null) {
			
			currentCounter = new Counter(name);
			
			counters.put(name, currentCounter);			
		}
		
		currentCounter.stopWatch.start();
		
	}
	
	public void stopCounter(long threadId, String name) {
		
		//Log.info(this, "stopping counter: " + name + " id: " + threadId);
		
		Map<String, Counter> counterMap = map.get(threadId);
		
		if(counterMap == null) {
			
			Log.error(this, "Thread id: " + threadId + " not found for counter: " + name);
			return;
		}
		
		Counter currentCounter = counterMap.get(name);
		
		if(currentCounter == null) {
			
			Log.error(this, "Thread id: " + threadId + " has never started counter: " + name);
			return;
		}
		
		currentCounter.stopWatch.stop();
		currentCounter.count++;
		currentCounter.timePassedMilliSeconds += currentCounter.stopWatch.timeMiliSeconds();
		
	}
	
	private void gatherResults() {
		
		resultList.clear();
		
	    Iterator<Map.Entry<Long, Map<String, Counter>>> threadIt = map.entrySet().iterator();
	    
	    while(threadIt.hasNext()) {
	    	
	        Map.Entry<Long, Map<String, Counter>> pairs = 
	        		(Map.Entry<Long, Map<String, Counter>>)threadIt.next();
	        
	        Iterator<Map.Entry<String, Counter>> counterIt = 
	        		pairs.getValue().entrySet().iterator();
	        
	        Long threadId = pairs.getKey();
	        
	        while(counterIt.hasNext()) {
	        
	        	 Map.Entry<String, Counter> counterPair = 
	        	   (Map.Entry<String, Counter>)counterIt.next();
	        	 
	        	 Counter counter = counterPair.getValue();
	        	 
	        	 int i;
	        	 
	        	 for(i = 0; i < resultList.size(); i++) {
	        		 
	        		 Result result = resultList.get(i);
	        		 
	        		 double avgTime = 0;
	        		 
	        		 if(counter.count > 0) {
	        		 
	        		 avgTime = counter.timePassedMilliSeconds / 
        					 counter.count;
	        		 }
	        		 
	        		 if(result.name.equals(counter.name)) {
	        			 
	        			 result.thread.add(counter);
	        			 result.totalCount += counter.count;
	        			 	        			 
	        			 result.nrCounters++;
	        			 result.avgTimeMilliSeconds += 
	        					 (avgTime - result.avgTimeMilliSeconds) / result.nrCounters;

	        			 result.threadId.add(threadId);
	        			 
	        			 i = 0;
	        			 
	        			 break;
	        			 
	        		 } 
	        		 
	        	 }
	        	 
	        	 if(i == resultList.size()) {
	        		 
	        		 Result result = new Result();
	        		 
	        		 result.name = counter.name;
	        		 result.nrCounters = 1;
	        		 result.thread.add(counter);
	        		 result.totalCount += counter.count;
	        		 
	        		 if(counter.count > 0) {
	        		 
	        		 result.avgTimeMilliSeconds = counter.timePassedMilliSeconds / 
        					 counter.count;
	        		 }
	        		 
	        		 result.threadId.add(threadId);
	        		 
	        		 resultList.add(result);
	        		 	        		 
	        	 }
	        	 
	        	 	        	
	        	 counterIt.remove();
	        }
	        
	        threadIt.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	synchronized public void printResults(boolean printThreadData, boolean clearResults) {
	
		gatherResults();
		
		for(int i = 0; i < resultList.size(); i++) {

			Result result = resultList.get(i);
			
			Log.info(this, "Counter Name: " + result.name);
			Log.info(this, "Overall Avg time: " + formatTime(result.avgTimeMilliSeconds));
			Log.info(this, "Overall Hits: " + result.totalCount);
			
			for(int j = 0; j < result.threadId.size() && printThreadData == true; j++) {
				
				//Long threadId = ;
				Counter counter = result.thread.get(j);
				
				double avgTime = counter.timePassedMilliSeconds / counter.count;
													
				Log.info(this, "\tThread id: " + result.threadId.get(j));
				Log.info(this, "\t\tHits: " + result.thread.get(j).count);
				Log.info(this, "\t\tAvg time: " +  formatTime(avgTime));
								
			}
			
			
		}
	
		// clear the map for the next run
		if(clearResults == true) {
			map.clear();
		}
	}
	
	private String formatTime(double miliSeconds) {
		
		if(miliSeconds < 1000) {
			
			return((long)miliSeconds + "ms");
		}
		
		long seconds = (long)miliSeconds / 1000;
		
		if(seconds < 60) {
		
			return(seconds + "s");		
		}
		
		long minutes = seconds / 60;
		seconds = seconds % 60;
		
		if(minutes < 60) {
		
			return(minutes + "min " + seconds + "s");		
		}
		
		long hours = minutes / 60;
		minutes = minutes % 60;
		
		return(hours + "h " + minutes + "min " + seconds + "s");	
	}
}
