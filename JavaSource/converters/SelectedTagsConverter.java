package converters;

import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.google.gson.Gson;

@FacesConverter("converters.SelectedTagsConverter")
public class SelectedTagsConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
	
		Gson gson = new Gson();
		
		String[] selectedTagsArray = gson.fromJson(value, String[].class);
		
		List<String> selectedTags = Arrays.asList(selectedTagsArray); 
		
		return (selectedTags);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
	
		@SuppressWarnings("unchecked")
		List<String> selectedTags = (List<String>) value;
		
		Gson gson = new Gson();
		
		String selectedTagsJson = gson.toJson(selectedTags);
		
		return(selectedTagsJson);
		
	}

}
