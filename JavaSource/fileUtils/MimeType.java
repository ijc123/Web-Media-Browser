package fileUtils;

import java.util.HashMap;
import java.util.Map;

public class MimeType {

	private static Map<String, String> extToMime;
	
	static {
		
		extToMime = new HashMap<String, String>();
		
	  	//
		extToMime.put("323","text/h323");
		extToMime.put("acx","application/internet-property-stream");
		extToMime.put("ai","application/postscript");
		extToMime.put("aif","audio/x-aiff");
		extToMime.put("aifc","audio/x-aiff");
		extToMime.put("aiff","audio/x-aiff");
		extToMime.put("asf","video/x-ms-asf");
		extToMime.put("asr","video/x-ms-asf");
		extToMime.put("asx","video/x-ms-asf");
		extToMime.put("au","audio/basic");
		extToMime.put("avi","video/x-msvideo");
		extToMime.put("axs","application/olescript");
		extToMime.put("bas","text/plain");
		extToMime.put("bcpio","application/x-bcpio");
		extToMime.put("bin","application/octet-stream");
		extToMime.put("bmp","image/bmp");
		extToMime.put("c","text/plain");
		extToMime.put("cat","application/vnd.ms-pkiseccat");
		extToMime.put("cdf","application/x-cdf");
		extToMime.put("cer","application/x-x509-ca-cert");
		extToMime.put("class","application/octet-stream");
		extToMime.put("clp","application/x-msclip");
		extToMime.put("cmx","image/x-cmx");
		extToMime.put("cod","image/cis-cod");
		extToMime.put("cpio","application/x-cpio");
		extToMime.put("crd","application/x-mscardfile");
		extToMime.put("crl","application/pkix-crl");
		extToMime.put("crt","application/x-x509-ca-cert");
		extToMime.put("csh","application/x-csh");
		extToMime.put("css","text/css");
		extToMime.put("dcr","application/x-director");
		extToMime.put("der","application/x-x509-ca-cert");
		extToMime.put("dir","application/x-director");
		extToMime.put("dll","application/x-msdownload");
		extToMime.put("dms","application/octet-stream");
		extToMime.put("doc","application/msword");
		extToMime.put("dot","application/msword");
		extToMime.put("dvi","application/x-dvi");
		extToMime.put("dxr","application/x-director");
		extToMime.put("eps","application/postscript");
		extToMime.put("etx","text/x-setext");
		extToMime.put("evy","application/envoy");
		extToMime.put("exe","application/octet-stream");
		extToMime.put("fif","application/fractals");
		extToMime.put("flr","x-world/x-vrml");
		extToMime.put("gif","image/gif");
		extToMime.put("gtar","application/x-gtar");
		extToMime.put("gz","application/x-gzip");
		extToMime.put("h","text/plain");
		extToMime.put("hdf","application/x-hdf");
		extToMime.put("hlp","application/winhlp");
		extToMime.put("hqx","application/mac-binhex40");
		extToMime.put("hta","application/hta");
		extToMime.put("htc","text/x-component");
		extToMime.put("htm","text/html");
		extToMime.put("html","text/html");
		extToMime.put("htt","text/webviewhtml");
		extToMime.put("ico","image/x-icon");
		extToMime.put("ief","image/ief");
		extToMime.put("iii","application/x-iphone");
		extToMime.put("ins","application/x-internet-signup");
		extToMime.put("isp","application/x-internet-signup");
		extToMime.put("jfif","image/pipeg");
		extToMime.put("jpe","image/jpeg");
		extToMime.put("jpeg","image/jpeg");
		extToMime.put("jpg","image/jpeg");
		extToMime.put("js","application/x-javascript");
		extToMime.put("latex","application/x-latex");
		extToMime.put("lha","application/octet-stream");
		extToMime.put("lsf","video/x-la-asf");
		extToMime.put("lsx","video/x-la-asf");
		extToMime.put("lzh","application/octet-stream");
		extToMime.put("m13","application/x-msmediaview");
		extToMime.put("m14","application/x-msmediaview");
		extToMime.put("m3u","audio/x-mpegurl");
		extToMime.put("m4v","video/x-m4v");
		extToMime.put("man","application/x-troff-man");
		extToMime.put("mdb","application/x-msaccess");
		extToMime.put("me","application/x-troff-me");
		extToMime.put("mht","message/rfc822");
		extToMime.put("mhtml","message/rfc822");
		extToMime.put("mid","audio/mid");
		extToMime.put("mkv","video/x-matroska");
		extToMime.put("mny","application/x-msmoney");
		extToMime.put("mov","video/quicktime");
		extToMime.put("movie","video/x-sgi-movie");
		extToMime.put("mp2","video/mpeg");
		extToMime.put("mp3","audio/mpeg");
		extToMime.put("mp4", "video/mp4");
		extToMime.put("mpa","video/mpeg");
		extToMime.put("mpe","video/mpeg");
		extToMime.put("mpeg","video/mpeg");
		extToMime.put("mpg","video/mpeg");
		extToMime.put("mpp","application/vnd.ms-project");
		extToMime.put("mpv2","video/mpeg");
		extToMime.put("ms","application/x-troff-ms");
		extToMime.put("mvb","application/x-msmediaview");
		extToMime.put("nws","message/rfc822");
		extToMime.put("oda","application/oda");
		extToMime.put("p10","application/pkcs10");
		extToMime.put("p12","application/x-pkcs12");
		extToMime.put("p7b","application/x-pkcs7-certificates");
		extToMime.put("p7c","application/x-pkcs7-mime");
		extToMime.put("p7m","application/x-pkcs7-mime");
		extToMime.put("p7r","application/x-pkcs7-certreqresp");
		extToMime.put("p7s","application/x-pkcs7-signature");
		extToMime.put("pbm","image/x-portable-bitmap");
		extToMime.put("pdf","application/pdf");
		extToMime.put("pfx","application/x-pkcs12");
		extToMime.put("pgm","image/x-portable-graymap");
		extToMime.put("pko","application/ynd.ms-pkipko");
		extToMime.put("pma","application/x-perfmon");
		extToMime.put("pmc","application/x-perfmon");
		extToMime.put("pml","application/x-perfmon");
		extToMime.put("pmr","application/x-perfmon");
		extToMime.put("pmw","application/x-perfmon");
		extToMime.put("png","image/png");
		extToMime.put("pnm","image/x-portable-anymap");
		extToMime.put("pot","application/vnd.ms-powerpoint");
		extToMime.put("ppm","image/x-portable-pixmap");
		extToMime.put("pps","application/vnd.ms-powerpoint");
		extToMime.put("ppt","application/vnd.ms-powerpoint");
		extToMime.put("prf","application/pics-rules");
		extToMime.put("ps","application/postscript");
		extToMime.put("pub","application/x-mspublisher");
		extToMime.put("qt","video/quicktime");
		extToMime.put("ra","audio/x-pn-realaudio");
		extToMime.put("ram","audio/x-pn-realaudio");
		extToMime.put("ras","image/x-cmu-raster");
		extToMime.put("rgb","image/x-rgb");
		extToMime.put("rmi","audio/mid");
		extToMime.put("roff","application/x-troff");
		extToMime.put("rtf","application/rtf");
		extToMime.put("rtx","text/richtext");
		extToMime.put("scd","application/x-msschedule");
		extToMime.put("sct","text/scriptlet");
		extToMime.put("setpay","application/set-payment-initiation");
		extToMime.put("setreg","application/set-registration-initiation");
		extToMime.put("sh","application/x-sh");
		extToMime.put("shar","application/x-shar");
		extToMime.put("sit","application/x-stuffit");
		extToMime.put("snd","audio/basic");
		extToMime.put("spc","application/x-pkcs7-certificates");
		extToMime.put("spl","application/futuresplash");
		extToMime.put("src","application/x-wais-source");
		extToMime.put("sst","application/vnd.ms-pkicertstore");
		extToMime.put("stl","application/vnd.ms-pkistl");
		extToMime.put("stm","text/html");
		extToMime.put("svg","image/svg+xml");
		extToMime.put("sv4cpio","application/x-sv4cpio");
		extToMime.put("sv4crc","application/x-sv4crc");
		extToMime.put("swf","application/x-shockwave-flash");
		extToMime.put("t","application/x-troff");
		extToMime.put("tar","application/x-tar");
		extToMime.put("tcl","application/x-tcl");
		extToMime.put("tex","application/x-tex");
		extToMime.put("texi","application/x-texinfo");
		extToMime.put("texinfo","application/x-texinfo");
		extToMime.put("tgz","application/x-compressed");
		extToMime.put("tif","image/tiff");
		extToMime.put("tiff","image/tiff");
		extToMime.put("tr","application/x-troff");
		extToMime.put("trm","application/x-msterminal");
		extToMime.put("tsv","text/tab-separated-values");
		extToMime.put("txt","text/plain");
		extToMime.put("uls","text/iuls");
		extToMime.put("ustar","application/x-ustar");
		extToMime.put("vcf","text/x-vcard");
		extToMime.put("vrml","x-world/x-vrml");
		extToMime.put("wav","audio/x-wav");
		extToMime.put("wcm","application/vnd.ms-works");
		extToMime.put("wdb","application/vnd.ms-works");
		extToMime.put("wks","application/vnd.ms-works");
		extToMime.put("wmf","application/x-msmetafile");
		extToMime.put("wmv","video/x-ms-wmv");
		extToMime.put("wps","application/vnd.ms-works");
		extToMime.put("wri","application/x-mswrite");
		extToMime.put("wrl","x-world/x-vrml");
		extToMime.put("wrz","x-world/x-vrml");
		extToMime.put("xaf","x-world/x-vrml");
		extToMime.put("xbm","image/x-xbitmap");
		extToMime.put("xla","application/vnd.ms-excel");
		extToMime.put("xlc","application/vnd.ms-excel");
		extToMime.put("xlm","application/vnd.ms-excel");
		extToMime.put("xls","application/vnd.ms-excel");
		extToMime.put("xlt","application/vnd.ms-excel");
		extToMime.put("xlw","application/vnd.ms-excel");
		extToMime.put("xof","x-world/x-vrml");
		extToMime.put("xpm","image/x-xpixmap");
		extToMime.put("xwd","image/x-xwindowdump");
		extToMime.put("z","application/x-compress");
		extToMime.put("zip","application/zip");
	
	}
	
	static public String getMimeTypeFromExt(String fileName) {
	
		int dot = fileName.lastIndexOf('.');
		
		String ext = fileName.substring(dot + 1).toLowerCase();
		
		String mimeType = extToMime.get(ext);
		
		if(mimeType == null) {
			
			mimeType = "application/octet-stream";
			
		}
	
		return(mimeType);
	}
}
