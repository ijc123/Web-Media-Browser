package custom.component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.faces.application.ViewHandler;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

import utils.FileUtilsLocal;

import database.SettingsEJB;

@FacesComponent("custom.component.UIPreview")
public class UIPreview extends UIOutput {
	
	public UIPreview() {

		super();

	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		
		// String clientId = getClientId(context);

		String uri = (String) getAttributes().get("uri");
	
		String filePath = uri.replace("file:/", "");
		
		try {

			filePath = URLDecoder.decode(filePath, "UTF-8");

		} catch (UnsupportedEncodingException e1) {
	
			e1.printStackTrace();
		}
		
		SettingsEJB settingsEJB = null;
		
		try {
			settingsEJB = (SettingsEJB) new InitialContext().lookup("java:module/SettingsEJB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String previewPath = settingsEJB.getSettings().getPreviewRootDirectory();
		
		FileUtilsLocal f = new FileUtilsLocal(previewPath);
		
		String hash = Integer.toString(filePath.hashCode());
		
		int dot = filePath.lastIndexOf('.');
		int sep = filePath.lastIndexOf('/');
		String fileName = filePath.substring(sep + 1, dot);
		String previewImage = hash + "/" + fileName + "_small.jpg"; 
		String outputPath = previewPath + hash + "/";
		
		if(!f.exists(previewImage)) {
						
			f.createDirectory(hash);
			
			String font = "H:/mtn-200808a-win32/cyberbit.ttf";

			String batch = String.format(
					"H:/mtn-200808a-win32/mtn -c 10 -r 1 -w 800 -P -i -f %s -O %s \"%s\"",
					font, outputPath, filePath);
									
			runExternalProcess(batch);
			
			f.moveDown(hash);
			
			f.rename(fileName + "_s.jpg", fileName + "_small.jpg");
			
			batch = String.format(
					"H:/mtn-200808a-win32/mtn -c 2 -r 32 -f %s -O %s -P -w 800 \"%s\"",
					font, outputPath, filePath);
		
			runExternalProcess(batch);
			
			f.rename(fileName + "_s.jpg", fileName + "_large.jpg");
		}
		
		ResponseWriter rw = context.getResponseWriter();
		
		renderPreviewImages(rw, outputPath + fileName);
		
	}

	public void runExternalProcess(String batch) {

		CommandLine cmdLine = CommandLine.parse(batch);

		DefaultExecutor executor = new DefaultExecutor();
		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		executor.setWatchdog(watchdog);

		try {

			executor.execute(cmdLine);

		} catch (ExecuteException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	void renderPreviewImages(ResponseWriter rw, String filePath) throws IOException {
	
		FacesContext context = FacesContext.getCurrentInstance();
		ViewHandler handler = context.getApplication().getViewHandler();
		String imageURL = handler.getActionURL(context, "/previewimage");
		
		rw.startElement("div", null);
		
		String previewURL = filePath + "_small.jpg";
	
		try {
					
			previewURL = URLEncoder.encode(previewURL, "UTF-8");
		
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
			previewURL = "";
		}
	
		String fullURL = imageURL + "?id=" + previewURL;			
				
		rw.startElement("img", null);
		rw.writeAttribute("src", fullURL, null);
		rw.writeAttribute("style", "height: 127px; float:left;", null);					
		rw.endElement("img");			
			
		rw.endElement("div");
	}
}
