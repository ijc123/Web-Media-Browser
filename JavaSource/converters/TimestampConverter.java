package converters;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("converters.TimestampConverter")
public class TimestampConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		Timestamp result = new Timestamp(0);
		
		try {
			Date date = DateFormat.getDateInstance().parse(value);
			
			result = new Timestamp(date.getTime());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return(result);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
	
		Timestamp timestamp = (Timestamp)value;
		
		Date date = new Date(timestamp.getTime());
		
		return(date.toString());
		
	}

}
