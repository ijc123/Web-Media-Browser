package custom.component;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent("custom.component.UIPager")
@ResourceDependency(library="javascript", name="pager.js")   
public class UIPager extends UICommand {

	public UIPager() {

		super();

	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		
		String pagerId = getClientId(context);
		UIComponent parent = this;
		while (!(parent instanceof UIForm)) parent = parent.getParent();
		String formId = parent.getClientId(context);

		ResponseWriter writer = context.getResponseWriter();    

		
		String shortDataTableId = (String) getAttributes().get("dataTableId");
		int showpages = toInt(getAttributes().get("showpages"));      

		// find the component with the given ID

		UIData data = (UIData) findComponent(shortDataTableId);
		
		String tableId = data.getClientId();

		int first = data.getFirst();
		int itemcount = data.getRowCount();
		int pagesize = data.getRows(); 
		if (pagesize <= 0) pagesize = itemcount;

		int pages = 0;
		
		if(itemcount == 0 && pagesize == 0) return;
		
		pages = itemcount / pagesize;		
		
		if (itemcount % pagesize != 0) pages++;

		int currentPage = first / pagesize;
		if (first >= itemcount - pagesize) currentPage = pages - 1;
		int startPage = 0;
		int endPage = pages;
		if (showpages > 0) { 
			
			int half = (showpages -1) / 2;
			
			int spill = 0;
			
			if(currentPage - half > 0) startPage = currentPage - half;
			else spill = half - currentPage;
				
			if(currentPage + half < pages) endPage = currentPage + half + spill + 1;
			else startPage -= currentPage + half - pages + 1;
			
			if(startPage < 0) startPage = 0;
			if(endPage > pages) endPage = pages;
			
		
			//startPage = (currentPage / showpages) * showpages;
			//endPage = Math.min(startPage + showpages, pages);
		}
		
		writer.startElement("div", this);
		writer.writeAttribute("id", pagerId + ":pager", null);
		writer.writeAttribute("style", "text-align: center; margin: 5px;", null);
		
		String firstButtonStyle = "rf-ds-btn rf-ds-btn-first";
		String lastButtonStyle = "rf-ds-btn rf-ds-btn-last";
		String numberButtonStyle =  "rf-ds-nmb-btn";
		String nextButtonStyle = "rf-ds-btn rf-ds-btn-next";
		String prevButtonStyle = "rf-ds-btn rf-ds-btn-next";
		String fastForwardButtonStyle = "rf-ds-btn rf-ds-btn-fastfwd";
		
		if(currentPage > 0) writeButton(writer, formId, pagerId, tableId, "««««", firstButtonStyle);
		else writeDisabledButton(writer, "««««", firstButtonStyle);
		
		if(startPage > 0) writeButton(writer, formId, pagerId, tableId, "««",  fastForwardButtonStyle);
		else writeDisabledButton(writer, "««",  fastForwardButtonStyle);
		
		if(currentPage > 0) writeButton(writer, formId, pagerId, tableId, "«", prevButtonStyle);
		else writeDisabledButton(writer, "«", prevButtonStyle);
		
		for (int i = startPage; i < endPage; i++) {
			
			if(i == currentPage)  writeDisabledButton(writer, "" + (i + 1), numberButtonStyle);
			else writeButton(writer, formId, pagerId, tableId, "" + (i + 1), numberButtonStyle);
		}

		if(currentPage < pages - 1) writeButton(writer, formId, pagerId, tableId, "»", nextButtonStyle);
		else writeDisabledButton(writer, "»", nextButtonStyle);

		if(currentPage + showpages < pages - 1) writeButton(writer, formId, pagerId, tableId, "»»", fastForwardButtonStyle);
		else writeDisabledButton(writer, "»»", fastForwardButtonStyle);
		
		if(currentPage < pages - 1) writeButton(writer, formId, pagerId, tableId,"»»»»", lastButtonStyle);
		else writeDisabledButton(writer, "»»»»", lastButtonStyle);
						
		writer.endElement("div");
		
		// hidden field to hold result
		writeHiddenField(writer, pagerId);
	}

	private void writeButton(ResponseWriter writer, String formId, String pagerId, String tableId, String value, String styleClass) throws IOException 
	{
		
		writer.writeText(" ", null);
		writer.startElement("a", this);
		writer.writeAttribute("href", "javascript:void(0);", null);
		writer.writeAttribute("onclick", onclickCode(formId, pagerId, tableId, value), null);
		writer.writeAttribute("class", styleClass, "styleClass");
		writer.writeText(value, null);
		writer.endElement("a");

	}
	
	private void writeDisabledButton(ResponseWriter writer, String value, String styleClass) throws IOException 
	{
		
		String disabledStyle = " rf-ds-act";
		
		writer.writeText(" ", null);
		writer.startElement("span", this);
		writer.writeAttribute("class", styleClass + disabledStyle, "styleClass");
		writer.writeText(value, null);
		writer.endElement("span");

	}
	
	

	private String onclickCode(String formId, String pagerId, String tableId, String value) {
		
		String setHiddenField = String.format("document.forms['%s']['%s'].value='%s';", formId, pagerId, value);
		//j_idt24:tagSearchForm:j_idt45:queryTable:table
		String ajax = String.format("jsf.ajax.request(this, null, {execute:'@form', render: '@form'});");
		//String ajax = String.format("jsf.ajax.request(this, null, {execute:'%s', render: '@form'});", pagerId);
		//String scrollTo = "var pos = $(this).offset(); window.scrollTo(pos.left, pos.top);";

		return setHiddenField + ajax;
		    
	}

	private void writeHiddenField(ResponseWriter writer,  String id) throws IOException {
		writer.startElement("input", this);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("name", id, null);
		writer.writeAttribute("id", id, null);
		writer.endElement("input");
	}

	@Override
	public void decode(FacesContext context) {
		
		String id = getClientId(context); 
		
		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap(); 

		String response = (String) parameters.get(id); 
		
		String dataTableId = (String) getAttributes().get("dataTableId"); 
		UIData data = (UIData) findComponent(dataTableId); 
		
		if (response == null || response.equals("")) {
			
			if(data != null) {
				//data.setFirst(0);
			}
			return; 
		}
	
		int showpages = toInt(getAttributes().get("showpages"));      

		int first = data.getFirst(); 
		int itemcount = data.getRowCount(); 
		int pagesize = data.getRows(); 
		if (pagesize <= 0) pagesize = itemcount; 

		if (response.equals("«")) first -= pagesize;
		else if (response.equals("»»»»")) first = ((itemcount - 1) / pagesize) * pagesize; 
		else if (response.equals("««««")) first = 0;		
		else if (response.equals("»")) first += pagesize; 
		else if (response.equals("««")) first -= pagesize * showpages; 
		else if (response.equals("»»")) first += pagesize * showpages; 
		else { 
			int page = Integer.parseInt(response); 
			first = (page - 1) * pagesize; 
		} 
		//if (first + pagesize > itemcount) first = itemcount - pagesize; 
		if (first < 0) first = 0; 
		data.setFirst(first); 
		
		//setSubmittedValue(first);
	} 

	private static int toInt(Object value) { 
		if (value == null) return 0; 
		if (value instanceof Number) return ((Number) value).intValue(); 
		if (value instanceof String) return Integer.parseInt((String) value); 
		throw new IllegalArgumentException("Cannot convert " + value); 
	}   
}
