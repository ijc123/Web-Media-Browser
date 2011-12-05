package beans;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import database.TypeItem;
import database.TypeTable;

@RequestScoped
@Named
public class TypeBean {
	
	@Inject
	private TypeTable typeTable;

	public List<String> getAllTypeNames() {

		List<TypeItem> types = typeTable.getAllTypes();

		List<String> typeName = new ArrayList<String>();

		for(int i = 0; i < types.size(); i++) {

			typeName.add(types.get(i).getName());
		}


		return(typeName);

	}
	
}
