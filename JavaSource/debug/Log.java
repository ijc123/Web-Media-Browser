package debug;

public class Log {

	static public void info(String classname, String message) {
			
		System.out.println("[" + classname + "]" + ": " + message);
		
	}
	
	static public void info(Object This, String message) {
		
		String name = This.getClass().getName();
		
		System.out.println("[" + name + "]" + ": " + message);
		
	}
	
	static public void error(String classname, String message) {
		
		System.err.println("[" + classname + "]" + ": " + message);
		
	}
	
	static public void error(Object This, String message) {
		
		String name = This.getClass().getName();
		
		System.err.println("[" + name + "]" + ": " + message);
		
	}
}
