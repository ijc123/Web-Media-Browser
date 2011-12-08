package beans;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.TypeItem;
import database.TypeEJB;

@RequestScoped
@Named
public class TypeBean {
	
	@Inject
	private TypeEJB typeEJB;

	public List<String> getAllTypeNames() {

		List<TypeItem> types = typeEJB.getAllTypes();

		List<String> typeName = new ArrayList<String>();

		for(int i = 0; i < types.size(); i++) {

			typeName.add(types.get(i).getName());
		}


		return(typeName);

	}
	
}
