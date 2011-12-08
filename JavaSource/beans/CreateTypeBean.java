package beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import database.TypeItem;
import database.TypeEJB;


@ViewScoped
@Named
public class CreateTypeBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private TypeEJB typeEJB;

	private DataModel<TypeItem> dataModel;
	private List<TypeItem> type;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void Init() {
		
		type = typeEJB.getAllTypes();
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
		
		typeEJB.updateAllTypes(type);
	}
	
	
}
