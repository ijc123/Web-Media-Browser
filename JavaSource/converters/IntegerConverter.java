package converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("converters.IntegerConverter")
public class IntegerConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		Integer integer = null;
		
		if(!value.isEmpty()) {
			
			integer = Integer.parseInt(value);
		}
		
		return(integer);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		
		String string = Integer.toString((Integer)value);
		
		return(string);
		
	}

}

