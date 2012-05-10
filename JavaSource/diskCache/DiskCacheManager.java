package diskCache;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.jcs.JCS;
import org.apache.jcs.engine.control.CompositeCacheManager;

import debug.Log;

import virtualFile.Location;
import virtualFile.VirtualInputFile;
import virtualFile.VirtualInputFileFactory;

public class DiskCacheManager {
	
	private class LoadedData {

		public DiskCacheElement elem;
		public VirtualInputFile input;	

		public LoadedData() {

			elem = null;
			input = null;			
		}

	}

	private static DiskCacheManager instance;
	@SuppressWarnings("unused")
	private static int checkedOut = 0;
	private static JCS diskCache;
	//private static int elementSizeBytes = 65536;
	private static int elementSizeBytes = 524288;

	private DiskCacheManager()
	{
		try
		{
			diskCache = JCS.getInstance("diskCache");
		}
		catch (Exception e)
		{
			// Handle cache region initialization failure
		}

		// Do other initialization that may be necessary, such as getting
		// references to any data access classes we may need to populate
		// value objects later
	}

	/**
	 * Singleton access point to the manager.
	 */
	public static DiskCacheManager getInstance()
	{
		synchronized (DiskCacheManager.class)
		{
			if(instance == null)
			{
				
				try {
					
					CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance(); 
					Properties props = new Properties(); 
					FileReader configFile = new FileReader("C:/Java/workspace/mijngod/JavaSource/diskCache/cache.ccf");
					props.load(configFile); 
					ccm.configure(props); 
					
					instance = new DiskCacheManager();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		synchronized(instance)
		{
			DiskCacheManager.checkedOut++;
		}

		return instance;
	}

	public void copy(Location location, OutputStream output, long start, long length)
		        throws IOException
	{
		
		long end = start + length;
		long offset, bufLength;
		
		long startElement = start / elementSizeBytes;
		long endElement = end / elementSizeBytes;
		
		VirtualInputFile input = null;
		
		for(long i = startElement; i <= endElement; i++) {
			
			String id = "DiskCache:" + location.getDecodedURL() + ":" + Long.toString(i);
			
			DiskCacheElement elem = (DiskCacheElement) diskCache.get(id);
			
			if(elem == null) {
				
				//Log.info(this, "cache miss: " + start + " " + length);
				LoadedData data = loadElement(id, location, i, input);
				
				elem = data.elem;
				input = data.input;
				
			} else {
				
				//Log.info(this, "cache HIT");
			}
			
			if(elem.getStart() > start) {
				
				offset = 0;
				
			} else {
				
				offset = start - elem.getStart();				
			}
			
			if(elem.getEnd() > end) {
				
				bufLength = end - elem.getStart() - offset;
				
			} else {
			
				bufLength = elem.getEnd() - elem.getStart() - offset;			
			}
				
			try {
				
				output.write(elem.getBuffer(),(int)offset, (int)bufLength);
				
				//Log.info(this, "id: " + Thread.currentThread().getId() + " offset: " + offset + " bufLength: " + bufLength + " " + i);
				
			} catch (IOException e) {
				
				Log.info(this, "ERROR id: " + Thread.currentThread().getId() + " offset: " + offset + " bufLength: " + bufLength + " " + i);
			    break;				
			}
		
		}
		
		if(input != null) {
			
			input.close();
		}
	}
	


	/**
	 * Creates a BookVObj based on the id of the BOOK table.  Data
	 * access could be direct JDBC, some or mapping tool, or an EJB.
	 */
	private LoadedData loadElement(String id, Location location, long i, VirtualInputFile input)
	{
		
		long start = i * elementSizeBytes;
		long end = start + elementSizeBytes;
		
		LoadedData data = new LoadedData();
		data.elem = new DiskCacheElement(id, start, end);
		
		try {
		
			if(input == null) {
				
				input = VirtualInputFileFactory.create(location);
			}
			
			data.input = input;
			
			if(end > input.length()) end = input.length();
			
			input.seek(start);
			
			int length = (int)(end - start);
			
			input.read(data.elem.getBuffer(), 0, length);
			
			diskCache.put(id, data.elem);
		
		}
		catch (Exception e)
		{
			// Handle failure putting object to cache
			e.printStackTrace();
		}

		return data;
	}

	void copyBufferedData() {
		
		
	}
	
}
