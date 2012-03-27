package custom.component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import virtualFile.FileUtilsLocal;
import virtualFile.Location;


@FacesRenderer(componentFamily = "javax.faces.SelectOne", rendererType = "custom.component.FileSelectOne")
@ResourceDependencies({
	@ResourceDependency(library="javax.faces", name="jsf.js"),
	@ResourceDependency(name = "jquery.js", library = "", target = ""),
	@ResourceDependency(name = "jquery-ui-1.8.10.custom.css", library = "css/ui-lightness", target = ""),
	@ResourceDependency(name = "jquery-ui-1.8.10.custom.min.js", library = "javascript", target = ""),	
	@ResourceDependency(library="javascript", name="fileSelectOne.js")   
	})
public class UIFileSelectRenderer extends Renderer {

	private static String convertToString(Object object) {
		return object != null ? object.toString() : "";
	}
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component)
			throws IOException {
		
		UIFileSelectOne fileSelect = (UIFileSelectOne) component;
		
		if(fileSelect.isFirstRender()) {
			
			getContents(fileSelect);
			
			fileSelect.setFirstRender(false);
		}
				
		ResponseWriter responseWriter = facesContext.getResponseWriter();
		String clientId = component.getClientId(facesContext);
		responseWriter.startElement("table", component);
		{
			
			String value = convertToString(clientId);
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}
		{
			
			String value = convertToString(clientId);
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		responseWriter.writeAttribute("style", "width:300px", null);
		
		responseWriter.startElement("tbody", component);

		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		// drive select
		
		responseWriter.startElement("select", component);
		{
			String value = convertToString(clientId) + ":selectDrive";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":selectDrive";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		{
			String value = "jsf.ajax.request(this,event,{execute: '"
					+ convertToString(clientId) + "', render: '"
					+ convertToString(clientId) + "'})";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("onchange", value, null);
			}

		}

		responseWriter.writeAttribute("size", "1", null);

		responseWriter.writeAttribute("style", "width: 100%", null);
		responseWriter.writeAttribute("value", fileSelect.getRootDrive(), null);
		
		List<String> roots = fileSelect.getRoots();
		
		for(int i = 0; i < roots.size(); i++) {
			
			String root = roots.get(i);
			
			responseWriter.startElement("option", component);
	
			responseWriter.writeAttribute("value", root, null);			
			
			if(root.equals(fileSelect.getRootDrive())) {
				
				responseWriter.writeAttribute("selected", "selected", null);
			}
						
			responseWriter.writeText(root, null);
											
			responseWriter.endElement("option");
		}
		
		
		responseWriter.endElement("select");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		// location
		
		responseWriter.startElement("input", component);
		responseWriter.writeAttribute("disabled", "disabled", null);

		{
			String value = convertToString(clientId) + ":location";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":location";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		responseWriter.writeAttribute("style", "width: 100%", null);

		responseWriter.writeAttribute("type", "text", null);

		responseWriter.writeAttribute("value", fileSelect.getLocation(), null);

		responseWriter.endElement("input");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		responseWriter.startElement("div", component);
		responseWriter.writeAttribute("class", "rf_jq_filepicker", null);

		{
			String value = convertToString(clientId) + ":ftpAccordeon";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}
				
		
		responseWriter.writeAttribute("style", "rf_jq_filepicker", null);

		responseWriter.startElement("h3", component);

		responseWriter.startElement("a", component);
		responseWriter.writeURIAttribute("href", "#", null);

		responseWriter.writeText("FTP", null);

		responseWriter.endElement("a");
		responseWriter.endElement("h3");
		responseWriter.startElement("div", component);

		responseWriter.startElement("table", component);
		{
			String value = convertToString(clientId) + ":ftpPanel";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		responseWriter.startElement("tbody", component);

		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		responseWriter.writeText("Host", null);

		responseWriter.endElement("td");
		responseWriter.startElement("td", component);

		// ftp adress
		
		responseWriter.startElement("input", component);
		{
			String value = convertToString(clientId) + ":ftpAdress";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":ftpAdress";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		responseWriter.writeAttribute("type", "text", null);
		responseWriter.writeAttribute("value", fileSelect.getFtpServer(), null);

		responseWriter.endElement("input");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		responseWriter.writeText("Username", null);

		responseWriter.endElement("td");
		responseWriter.startElement("td", component);

		// Ftp username
		
		responseWriter.startElement("input", component);
		{
			String value = convertToString(clientId) + ":username";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":username";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		responseWriter.writeAttribute("type", "text", null);
		responseWriter.writeAttribute("value", fileSelect.getFtpUsername(), null);

		responseWriter.endElement("input");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		responseWriter.writeText("Password", null);

		responseWriter.endElement("td");
		responseWriter.startElement("td", component);

		// ftp password
		
		responseWriter.startElement("input", component);
		{
			String value = convertToString(clientId) + ":password";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":password";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		responseWriter.writeAttribute("type", "text", null);
		responseWriter.writeAttribute("value", fileSelect.getFtpPassword(), null);

		responseWriter.endElement("input");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		responseWriter.startElement("input", component);
		{
			String value = convertToString(clientId) + ":ftpConnect";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":ftpConnect";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		{
			String value = "jsf.ajax.request(this,event,{execute: '"
					+ convertToString(clientId) + "', render: '"
					+ convertToString(clientId) + "'});return false";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("onclick", value, null);
			}

		}

		responseWriter.writeAttribute("type", "submit", null);

		responseWriter.writeAttribute("value", "connect", null);

		responseWriter.endElement("input");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);
		responseWriter.writeAttribute("style", "color: red; font: italic;",
				null);

		responseWriter.writeText(!fileSelect.getFtpConnectMessage().isEmpty() ? "Error: " : "", null);

		responseWriter.endElement("td");
		responseWriter.startElement("td", component);
		responseWriter.writeAttribute("style", "color: red; font: italic;",
				null);

		responseWriter.writeText(fileSelect.getFtpConnectMessage(), null);

		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.endElement("tbody");
		responseWriter.endElement("table");
		responseWriter.endElement("div");
		responseWriter.startElement("script", component);
		responseWriter.writeAttribute("type", "text/javascript", null);
				
		
		{
			Object text = "jQuery(function() {\n\t\t\t\t\t                $(document.getElementById('"
					+ convertToString(clientId)
					+ ":ftpAccordeon')).accordion({ collapsible: true, active: false });\n\t\t\t\t\t            });";
			if (text != null) {
				responseWriter.writeText(text, null);
			}
		}

		responseWriter.endElement("script");
		responseWriter.endElement("div");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.startElement("tr", component);

		responseWriter.startElement("td", component);

		responseWriter.startElement("select", component);
		{
			String value = convertToString(clientId) + ":selectFile";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		{
			String value = convertToString(clientId) + ":selectFile";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("name", value, null);
			}

		}

		{
			String value = "jsf.ajax.request(this,event,{execute: '"
					+ convertToString(clientId) + "', render: '"
					+ convertToString(clientId) + "'})";
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("ondblclick", value, null);
			}

		}

		responseWriter.writeAttribute("size", "10", null);

		responseWriter.writeAttribute("style", "width: 100%", null);

		List<String> contents = fileSelect.getContents();
				
		for(int i = 0; i < contents.size(); i++) {
			
			String name = contents.get(i);
			
			responseWriter.startElement("option", component);
	
			responseWriter.writeAttribute("value", name, null);
									
			responseWriter.writeText(name, null);
											
			responseWriter.endElement("option");
		}
		
		
		responseWriter.endElement("select");
		responseWriter.endElement("td");
		responseWriter.endElement("tr");
		responseWriter.endElement("tbody");
		responseWriter.endElement("table");

	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		
		UIFileSelectOne fileSelect = (UIFileSelectOne)component;
		String clientId = fileSelect.getClientId(context);
					
		try {
			
			Location location = new Location(fileSelect.getLocation());
			
			// initialize rootdrive

			if(location.getProtocol().equals("file")) {

				fileSelect.setRootDrive(location.getHost() + "/");

			} else if(fileSelect.getRootDrive() == null) {

				fileSelect.setRootDrive(FileUtilsLocal.getRootPaths().get(0));
				
			}

			// changed directory?
			
			Map<String, String> requestMap = 
					context.getExternalContext().getRequestParameterMap();

			String selected = requestMap.get(clientId + ":selectFile");

			if(selected != null && selected.endsWith("/")) {

				if(selected.equals("../")) {
					
					location.moveUp();

				} else {

					location.moveDown(selected);
				}

			} else {

				// changed the root drive?

				String selectDrive = requestMap.get(clientId + ":selectDrive");

				if (selectDrive != null && 
						!selectDrive.equals(fileSelect.getRootDrive())) {

					fileSelect.setRootDrive(selectDrive);
					location = new Location(selectDrive);				

				}

				// pressed the ftp button?

				String source = requestMap.get("javax.faces.source");

				if(source != null && source.equals(clientId + ":ftpConnect")) {

					String ftpServer = requestMap.get(clientId + ":ftpAdress");

					fileSelect.setFtpServer(ftpServer);

					String ftpUsername = requestMap.get(clientId + ":username");

					fileSelect.setFtpUsername(ftpUsername);

					String ftpPassword = requestMap.get(clientId + ":password");

					fileSelect.setFtpPassword(ftpPassword);

					location = new Location(ftpServer);
					location.setUsername(ftpUsername);
					location.setPassword(ftpPassword);

				}

			}

			fileSelect.setLocation(location.getDecodedURL());		
			
			getContents(fileSelect);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void getContents(UIFileSelectOne fileSelect) {
		
		String location = fileSelect.getLocation();
		
		List<String> roots = FileUtilsLocal.getRootPaths();		
		fileSelect.setRoots(roots);
				
		if(location.isEmpty()) {
		
			if(roots.contains("c:/")) location = "c:/";
			else location = roots.get(0);
			
			fileSelect.setLocation(location);
		}
		
		ArrayList<FileInfo> contents = new ArrayList<FileInfo>();
		ArrayList<String> sortedContents = new ArrayList<String>();
		
		FileUtils f = null;
		
		try {
						 
			f = FileUtilsFactory.create(location);
			
			if(f.getLocation().getProtocol().equals("file")) {
				
				fileSelect.setRootDrive(f.getLocation().getHost() + "/");
			}
						
			f.getDirectoryContents(contents);
			
			if(!fileSelect.isHideDirectories()) {
			
				sortedContents.add("../");
				
				for(int i = 0; i < contents.size(); i++) {
					
					if(contents.get(i).isDirectory()) {
						
						sortedContents.add(contents.get(i).getName() + "/");
					}				
				}								
			}
			
			if(!fileSelect.isHideFiles()) {
				
				for(int i = 0; i < contents.size(); i++) {
					
					if(!contents.get(i).isDirectory()) {
						
						sortedContents.add(contents.get(i).getName());					
					}					
				}								
			}
			
			fileSelect.setContents(sortedContents);
			
			fileSelect.setFtpConnectMessage("");
			
		} catch (IOException e) {
			
			fileSelect.setFtpConnectMessage(e.getMessage());
			
			fileSelect.setContents(sortedContents);
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (URISyntaxException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			
			if(f != null) {
				
				f.close();
			}
		}
		
	}
	

}
