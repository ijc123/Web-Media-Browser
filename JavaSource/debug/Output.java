package debug;

public class Output {

	static public void info(Object This, String message) {
		
		String name = This.getClass().getName();
		
		System.out.println("(" + name + ")" + ": " + message);
		
	}
	
	static public void error(Object This, String message) {
		
		String name = This.getClass().getName();
		
		System.err.println("(" + name + ")" + ": " + message);
		
	}
}
