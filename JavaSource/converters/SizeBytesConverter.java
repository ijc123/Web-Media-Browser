package converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("converters.SizeBytesConverter")
public class SizeBytesConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		return(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
	
	
		long sizeBytes =  (Long)value;
		float sizeBytesF = sizeBytes;
		
		long kbBytes = 1024;
		long mbBytes = 1048576;
		long gbBytes = 1073741824;
				
		if(sizeBytes < kbBytes) {
			
			return("<1kb");			
		}
		
		if(sizeBytes < mbBytes) {
			
			long sizeKB = sizeBytes / kbBytes;
			
			return(Long.toString(sizeKB) + " KB");			
		}
		
		if(sizeBytes < gbBytes) {
			
			float sizeMB = sizeBytesF / mbBytes;
			
			return(String.format("%.2f", sizeMB) + " MB");	
			
		}
			
		float sizeGB = sizeBytesF / gbBytes;
		
		return(String.format("%.2f", sizeGB) + " GB");		
		
	}

}
