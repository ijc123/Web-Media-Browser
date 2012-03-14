package custom.component;

import java.io.IOException;
import java.net.MalformedURLException;
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
						
		String location = fileSelect.getLocation();
		
		FileUtils f;
		try {
			f = FileUtilsFactory.create(location);

			if (f.getClass().getName().equals("virtualFile.FileUtilsLocal")) {

				FileUtilsLocal fl = (FileUtilsLocal) f;

				fileSelect.setRootDrive(fl.getHost() + "/");

			} else {

				if (fileSelect.getRootDrive() == null) {

					fileSelect.setRootDrive(FileUtilsLocal.getRootPaths().get(0));
				}
			}

			Map<String, String> requestMap = 
					context.getExternalContext().getRequestParameterMap();

			// changed directory?

			String selected = requestMap.get(clientId + ":selectFile");

			if (selected != null && selected.endsWith("/")) {

				if (selected.equals("../")) {
					f.moveUp();

				} else {

					f.moveDown(selected);
				}

			} else {

				// changed the root drive?

				String selectDrive = requestMap.get(clientId + ":selectDrive");

				if (selectDrive != null && 
						!selectDrive.equals(fileSelect.getRootDrive())) {

					fileSelect.setRootDrive(selectDrive);
					f = FileUtilsFactory.create(selectDrive);

				}

				// pressed the ftp button?

				String source = requestMap.get("javax.faces.source");

				if (source != null && source.equals(clientId + ":ftpConnect")) {

					String ftpServer = requestMap.get(clientId + ":ftpAdress");

					fileSelect.setFtpServer(ftpServer);

					String ftpUsername = requestMap.get(clientId + ":username");

					fileSelect.setFtpUsername(ftpUsername);

					String ftpPassword = requestMap.get(clientId + ":password");

					fileSelect.setFtpPassword(ftpPassword);

					Location l;

					l = new Location(ftpServer);
					l.setUsername(ftpUsername);
					l.setPassword(ftpPassword);

					f = FileUtilsFactory.create(l.getLocation());

				}

			}

			fileSelect.setLocation(f.getLocation());
			getContents(fileSelect);

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void getContents(UIFileSelectOne fileSelect) {
/*		
		if(fileSelect.isFirstRender()) {
		
			fileSelect.setLocation((String)fileSelect.getValue());
			
		}
*/				
		String location = (String) fileSelect.getLocation();
		
		List<String> roots = FileUtilsLocal.getRootPaths();		
		fileSelect.setRoots(roots);
				
		if(location.isEmpty()) {
		
			if(roots.contains("c:/")) location = "c:/";
			else location = roots.get(0);
			
			fileSelect.setLocation(location);
		}
		
		ArrayList<FileInfo> contents = new ArrayList<FileInfo>();
		ArrayList<String> sortedContents = new ArrayList<String>();
		
		try {
			
			FileUtils f = FileUtilsFactory.create(location);
			
			if(f.getClass().getName().equals("virtualFile.FileUtilsLocal")) {
				
				FileUtilsLocal fl = (FileUtilsLocal)f;
				
				fileSelect.setRootDrive(fl.getHost() + "/");
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
		}
		
	}
	
/*	
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {

		String clientId = component.getClientId(context);
		//String formID = getFormId(context, component);

		UIFileSelectOne fileSelect = (UIFileSelectOne) component;
				
		MediaLocationItem media = (MediaLocationItem) fileSelect.getValue();
		String rootDir = fileSelect.getRootDir();
							
		String uri = media.getURI();
		
		FileUtils f = FileUtilsFactory.create(uri);
		
		if(rootDir == "") {
					
			if(f.getClass().getName().equals("FileUtilsLocal")) {
							
				FileUtilsLocal fL = (FileUtilsLocal)f;
				
				fileSelect.setRootDir(fL.getDrive());
			}
		}
			
		ArrayList<FileInfo> directories = new ArrayList<FileInfo>();
		ArrayList<FileInfo> files = new ArrayList<FileInfo>();
				
		f.getDirectoryContents(directories, files);
		
		FileInfo moveUp = new FileInfo("..", "", 0, true);
		directories.add(0, moveUp);
		
		ResponseWriter rw = context.getResponseWriter();

		rw.startElement("table", fileSelect);
		rw.writeAttribute("id", clientId, null);
		
		// select drive
		
		rw.startElement("tr", fileSelect);
		rw.startElement("td", fileSelect);
		
		rw.startElement("select", fileSelect);
		rw.writeAttribute("name", clientId + ":selectDrive", null);
		rw.writeAttribute("id", clientId + ":selectDrive", null);
		rw.writeAttribute("size", 1, null);	
		rw.writeAttribute("style", "width:400px", null);
		
		ArrayList<String> rootPaths = FileUtilsLocal.getRootPaths();
		
		for(int i = 0; i < rootPaths.size(); i++) {
			
			rw.startElement("option", fileSelect);
	
			rw.writeAttribute("value", rootPaths.get(i), null);
			
			if(rootPaths.get(i).equals(rootDir)) {
				
				rw.writeAttribute("selected", "selected", null);
			}
			
			String ajaxScript = getAjaxRootDirScript(fileSelect, rootPaths.get(i));
			
			rw.writeAttribute("onclick", ajaxScript, null);
						
			rw.writeText(rootPaths.get(i), null);
											
			rw.endElement("option");
		}
		
		rw.endElement("td");
		rw.endElement("tr");
		
		// full path
		
		rw.startElement("tr", fileSelect);
		rw.startElement("td", fileSelect);
		
		rw.startElement("input", fileSelect);
		rw.writeAttribute("type", "text", null);
		rw.writeAttribute("readonly", "readonly", null);
		rw.writeAttribute("id", "fullPath", null);
		rw.writeAttribute("name", "fullPath", null);
		rw.writeAttribute("value", uri, null);
		rw.writeAttribute("style", "width:400px", null);
		rw.endElement("input");
		
		rw.endElement("td");
		rw.endElement("tr");

		// select file/directory
		
		rw.startElement("tr", fileSelect);
		rw.startElement("td", fileSelect);
		
		rw.startElement("select", fileSelect);
		rw.writeAttribute("name", "selectFile", null);
		rw.writeAttribute("size", fileSelect.getSize(), null);	
		rw.writeAttribute("style", "width:400px; overflow:auto", null);	
		
		if(fileSelect.isHideDirectories() == false) {

			for(int i = 0; i < directories.size(); i++) {
	
				rw.startElement("option", fileSelect);
		
				String ajaxScript = getAjaxSelectionScript(fileSelect, directories.get(i).getName() + "/");
				
				rw.writeAttribute("ondblclick", ajaxScript, null);
				
				//rw.writeAttribute("value", directories.get(i) + "/", null);
				rw.writeText(directories.get(i).getName() + "/", null);
												
				rw.endElement("option");
			}

			fileSelect.setDirectories(directories);
			
		} else {
			
			fileSelect.setDirectories(null);
		}
		
		if(fileSelect.isHideFiles() == false) {

			for (int i = 0; i < files.size(); i++) {
	
				rw.startElement("option", fileSelect);
				
				String ajaxScript = getAjaxSelectionScript(fileSelect, files.get(i).getName());
				
				rw.writeAttribute("ondblclick", ajaxScript, null);
	
				//rw.writeAttribute("value", files.get(i), null);
				rw.writeText(files.get(i).getName(), null);					
				
				rw.endElement("option");
			}

			fileSelect.setFiles(files);
			
		} else {
			
			fileSelect.setFiles(null);
		}
		
		rw.endElement("select");
		
		rw.endElement("td");
		rw.endElement("tr");	
		rw.endElement("table");
	}


	public void decode(FacesContext context, UIComponent component) {
		
		Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();

		String selected = requestMap.get("selectionString");
		
		UIFileSelectOne fileSelect = (UIFileSelectOne)component;
		
		if(selected != null && selected.endsWith("/")) {
			
			MediaLocationItem media = (MediaLocationItem) fileSelect.getValue();
			
			String uri = media.getURI();
			
			FileUtils f = FileUtilsFactory.create(uri);
							
			if(selected.equals("../"))
			{
				f.moveUp();
					
			} else {
				
				f.moveDown(selected); 
			}
				
			fileSelect.setValue(f.getPath());
		}
	
		String rootDir = requestMap.get("rootDir");
		
		if(rootDir != null && !rootDir.equals(fileSelect.getRootDir())) {
						
			fileSelect.setRootDir(rootDir);
			fileSelect.setValue(rootDir.toLowerCase());
		}
	
			FileUtils f = FileUtilsFactory.create(location);
		
		if(f.getClass().getName().equals("FileUtilsLocal")) {
			
			FileUtilsLocal fl = (FileUtilsLocal)f;
			
			fileSelect.setRootDrive(fl.getDrive());
		}
	}
	
	public static String getFormId(FacesContext context, UIComponent component) {

		UIComponent curComponent = component;

		while (!(curComponent instanceof UIForm)) {

			curComponent = curComponent.getParent();
		}

		return (curComponent.getClientId(context));
	}
	
	protected final String getAjaxSelectionScript(UIComponent component, String selectionString) {
		
		String comma = ",";
		
		String clientID = component.getClientId();
		String request = "jsf.ajax.request('" + clientID + "', null, {";
		String render =  "render: '" + component.getClientId() + "'";
		String selection = "selectionString: '" + selectionString + "'";
		String end = "})";
		
		String script = request + render + comma + selection + end;
		
		return script;
	}
	
	protected final String getAjaxRootDirScript(UIComponent component, String rootDir) {
		
		String comma = ",";
		
		String clientID = component.getClientId();
		String request = "jsf.ajax.request('" + clientID + "', null, {";
		String render =  "render: '" + component.getClientId() + "'";
		String selection = "rootDir: '" + rootDir + "'";
		String end = "})";
		
		String script = request + render + comma + selection + end;
		
		return script;
	}
*/
}
