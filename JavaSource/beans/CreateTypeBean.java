package beans;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import database.TypeItem;
import database.TypeTable;

@ManagedBean
@ViewScoped
public class CreateTypeBean {
	
	@EJB
	private TypeTable typeTable;

	private DataModel<TypeItem> dataModel;
	private List<TypeItem> type;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void Init() {
		
		type = typeTable.getAllTypes();
	}
	
	public DataModel<TypeItem> getDataModel() {
		
		dataModel = new ListDataModel<TypeItem>(type);
		
		return dataModel;
	}

	public void addType() {
			
		TypeItem newType = new TypeItem();
		
		String uniqueName = "Newtype";
		
		int j = 0;
		
		for(int i = 0; i < type.size(); i++) {
			
			if(type.get(i).getName().equals(uniqueName + Integer.toString(j))) {
				
				i = 0;
				j++;
			}
			
		}
		
		newType.setName(uniqueName + Integer.toString(j));
		
		java.util.Date today = new java.util.Date();
		
		newType.setCreation(new Timestamp(today.getTime()));
		newType.setModified(new Timestamp(today.getTime()));
	
		type.add(newType);
		
		
	}
	
	public void deleteType() {
		
		int index = dataModel.getRowIndex();
				
		type.remove(index);
		
	}
	
	public String getName() {
		
		int index = dataModel.getRowIndex();
		
		return(type.get(index).getName());
		
	}
	
	public void setName(String name) {
		
		int index = dataModel.getRowIndex();
		
		if(!type.get(index).getName().equals(name)) {
												
			type.get(index).setName(name);
		}
		
	}
	
	public void update() {
		
		typeTable.updateAllTypes(type);
	}
	
	
}
