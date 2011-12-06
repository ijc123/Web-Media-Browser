package custom.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import utils.FileUtils;


@FacesRenderer(componentFamily = "javax.faces.SelectOne", rendererType = "custom.component.FileSelectOne")
@ResourceDependencies({
	@ResourceDependency(library="javax.faces", name="jsf.js"),
	@ResourceDependency(library="javascript", name="fileSelectOne.js")   
	})
public class UIFileSelectRenderer extends Renderer {

	
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {

		String clientId = component.getClientId(context);
		//String formID = getFormId(context, component);

		UIFileSelectOne fileSelect = (UIFileSelectOne) component;
				
		String currentPath = (String) fileSelect.getValue();
				
		FileUtils f = new FileUtils(currentPath);
		
		String rootDir = fileSelect.getRootDir();
				
		if(rootDir == "") {
			
			String[] result = FileUtils.splitPath(currentPath);
			rootDir = result[0].toUpperCase() + "/";
			fileSelect.setRootDir(rootDir);
						
		}
			
		ArrayList<String> directories = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();
		
		f.getDirectoryContents(directories, files);
		directories.add(0,"..");
		
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
		
		ArrayList<String> rootPaths = f.getRootPaths();
		
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
		rw.writeAttribute("value", currentPath, null);
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
		
				String ajaxScript = getAjaxSelectionScript(fileSelect, directories.get(i) + "/");
				
				rw.writeAttribute("ondblclick", ajaxScript, null);
				
				//rw.writeAttribute("value", directories.get(i) + "/", null);
				rw.writeText(directories.get(i) + "/", null);
												
				rw.endElement("option");
			}

			fileSelect.setDirectories(directories);
			
		} else {
			
			fileSelect.setDirectories(null);
		}
		
		if(fileSelect.isHideFiles() == false) {

			for (int i = 0; i < files.size(); i++) {
	
				rw.startElement("option", fileSelect);
				
				String ajaxScript = getAjaxSelectionScript(fileSelect, files.get(i));
				
				rw.writeAttribute("ondblclick", ajaxScript, null);
	
				//rw.writeAttribute("value", files.get(i), null);
				rw.writeText(files.get(i), null);					
				
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
/*	
		Iterator it = requestMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> pairs = (Map.Entry<String, String>)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
*/	
		String selected = requestMap.get("selectionString");
		
		UIFileSelectOne fileSelect = (UIFileSelectOne)component;
		
		if(selected != null && selected.endsWith("/")) {
			
			FileUtils f = new FileUtils((String)fileSelect.getValue());
							
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
/*
	protected final String getSelectDirectoryScript(String formID) {
		
		return "setSelectedDirectory(document.forms['" + formID + "']);";

		
	}
*/
}
