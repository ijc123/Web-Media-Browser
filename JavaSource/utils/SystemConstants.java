package utils;

public class SystemConstants {

	static private int jbossPort = 8080;
	static private String lanHostAdress = "192.168.1.4";
	static private String wanHostAdress = "192.168.1.4";

	public static int getJbossPort() {
		return jbossPort;
	}

	public static String getLanAdress() {
		return "http://" + lanHostAdress + ":" + Integer.toString(jbossPort);
	}
	
	public static String getWanAdress() {
		return "http://" + wanHostAdress + ":" + Integer.toString(jbossPort);
	}
	
	public static String getLanHostAdress() {
		return lanHostAdress;
	}

	public static String getWanHostAdress() {
		return wanHostAdress;
	}

}
