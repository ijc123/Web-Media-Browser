package beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import database.TypeItem;
import database.TypeTable;

@ManagedBean
@RequestScoped
public class TypeBean {
	
	@EJB
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
