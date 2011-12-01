package database;

import java.sql.Timestamp;

public class TypeItem {

	private int id;
	private String name;
	private Timestamp creation;
	private Timestamp modified;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getCreation() {
		return creation;
	}
	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	@Override
	public boolean equals(Object other) {
		
	    if (other == this) return true;
	    if (other == null) return false;
	    if (getClass() != other.getClass()) return false;
	    
	    TypeItem type = (TypeItem)other;
	    
	    return (id == type.getId() &&
	    		name.equals(type.getName()) && 
	    		creation.equals(type.getCreation()) &&
	    		modified.equals(type.getModified()));
	
	}

	
}
