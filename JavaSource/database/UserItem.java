package database;

import java.util.List;

public class UserItem {

	private int id;
	private String name;
	private String password;
	private List<String> accessTypes;
	private int level;
	
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
