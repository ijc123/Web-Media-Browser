package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import database.UserEJB;
import database.UserItem;

@ViewScoped
@Named
public class CreateUserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Inject
	private UserEJB userEJB;

	@Inject
	private TypeBean typeBean;
	
	private DataModel<UserItem> dataModel;
	private List<DataModel<String>> userAccessTypeDataModel;
	private List<UserItem> user;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void Init() {
		
		user = userEJB.getAllUsers();
		
		updateDataModel();
		
	}
	
	private void updateDataModel() {
		
		dataModel = new ListDataModel<UserItem>(user);
		
		userAccessTypeDataModel = new ArrayList<DataModel<String>>();
		
		for(int i = 0; i < user.size(); i++) {
			
			userAccessTypeDataModel.add(new ListDataModel<String>(user.get(i).getAccessTypes()));
		}
		
	}
	
	public DataModel<UserItem> getDataModel() {
				
		return dataModel;
	}

	public void addUser() {
			
		UserItem newUser = new UserItem();
		
		String uniqueName = "NewUser";
		
		int j = 0;
		
		for(int i = 0; i < user.size(); i++) {
			
			if(user.get(i).getName().equals(uniqueName + Integer.toString(j))) {
				
				i = 0;
				j++;
			}
			
		}
		
		newUser.setName(uniqueName + Integer.toString(j));
		newUser.setPassword("password");
		
		List<String> typeNames = typeBean.getAllTypeNames();
		
		List<String> accessTypes = newUser.getAccessTypes();
		
		accessTypes.add(typeNames.get(0));
			
		newUser.setAccessTypes(accessTypes);
		
		user.add(newUser);
		
		updateDataModel();
	}
	
	public void deleteUser() {
		
		int index = dataModel.getRowIndex();
				
		user.remove(index);
		
		updateDataModel();
		
	}
	
	public DataModel<String> getUserAccesTypesDataModel() {
		
		int index = dataModel.getRowIndex();
		
		return(userAccessTypeDataModel.get(index));
		
	}
	
	public String getUserAccessType() {
		
		int userIndex = dataModel.getRowIndex();
		
		int accessTypeIndex = userAccessTypeDataModel.get(userIndex).getRowIndex();
			
		return(user.get(userIndex).getAccessTypes().get(accessTypeIndex));
				
	}
	
	public void setUserAccessType(String type) {
		
		int userIndex = dataModel.getRowIndex();
		
		int accessTypeIndex = userAccessTypeDataModel.get(userIndex).getRowIndex();
			
		user.get(userIndex).getAccessTypes().add(accessTypeIndex, type);
		user.get(userIndex).getAccessTypes().remove(accessTypeIndex + 1);
		
	}
	
	public void addUserAccessType() {
		
		int userIndex = dataModel.getRowIndex();
		
		List<String> accessTypes = user.get(userIndex).getAccessTypes();
		
		List<String> typeNames = typeBean.getAllTypeNames();
		
		accessTypes.add(typeNames.get(0));
		
		user.get(userIndex).setAccessTypes(accessTypes);
		
		//updateDataModel();
		
	}
	
	public void removeUserAccessType() {
	
		int userIndex = dataModel.getRowIndex();
		
		int accessTypeIndex = userAccessTypeDataModel.get(userIndex).getRowIndex();
			
		List<String> accessTypes = user.get(userIndex).getAccessTypes();
		
		accessTypes.remove(accessTypeIndex);
		
		user.get(userIndex).setAccessTypes(accessTypes);
		
		//updateDataModel();
	}
	
	public void update() {
		
		userEJB.updateAllUsers(user);
	}
	

}
