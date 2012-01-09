package database;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserItem {

	private int id;
	
	@Pattern(regexp="[a-zA-Z0-9]+", message="Usernames can only contain letters and numbers.")
	@Size(min=4, max=16, message="Usernames should have between 4 to 16 characters.")
	private String name;
	
	@Size(min=4, max=16, message="Passwords should have between 4 to 16 characters.")
	private String password;
	
	private List<String> accessTypes;
	
	@Min(0)
	private int level;
	
	public UserItem() {
		
		id = 0;
		name = "";
		password = "";
		accessTypes = new ArrayList<String>();
		level = 0;		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getAccessTypes() {
		return accessTypes;
	}
	public void setAccessTypes(List<String> accessTypes) {
		this.accessTypes = accessTypes;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
	
}
