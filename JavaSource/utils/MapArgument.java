package utils;

import java.util.HashMap;
import java.util.Map;

public class MapArgument {

	public static Map<String, Object> create(Object... args)
	{
	    Map<String, Object> map = new HashMap<String, Object>();
	    	    
	    for (int i = 0; i < args.length; i += 2)
	    {
	        map.put((String)args[i], args[i+1]);
	    }
	    
	    return map;
	}
	
}
