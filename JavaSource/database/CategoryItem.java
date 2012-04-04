package database;

import java.io.Serializable;

import javax.validation.constraints.Size;

import debug.Log;

public class CategoryItem implements Cloneable, Comparable<CategoryItem>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Size(min=1, max=512, message="Invalid category name length.")
	private String name;

	private String typeName;

	public CategoryItem() {

		name = "";
		typeName = "";
	}
	
	public CategoryItem(String fullCategory) {

		int typeSlash = fullCategory.indexOf("/", 1);
		if(typeSlash == -1) typeSlash = fullCategory.length();
		
		name = fullCategory.substring(typeSlash);				
		typeName = fullCategory.substring(1, typeSlash);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getFullName() {

		return("/" + typeName + name);
	}

	public void setShortName(String shortName) {
		
		String temp = getFullName().substring(0, getFullName().lastIndexOf("/"));
		
		String newFullName = temp + "/" + shortName;
		
		CategoryItem tempCategory = new CategoryItem(newFullName);
		
		name = tempCategory.getName();
		typeName = tempCategory.getTypeName();
		
	}
	
	public String getShortName() {
		
		String fullName = getFullName();
		
		String shortName = fullName.substring(fullName.lastIndexOf("/") + 1, fullName.length());
		
		return(shortName);		
	}
	
	@Override
	public boolean equals(Object other) {

		if (other == this) return true;
		if (other == null) return false;
		if (getClass() != other.getClass()) return false;

		CategoryItem category = (CategoryItem)other;

		return (name.equals(category.getName()) && 
				typeName.equals(category.getTypeName()));

	}

	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException cnse) 
		{
			Log.error(this, "CloneNotSupportedException thrown " + cnse);
			throw new CloneNotSupportedException();
		}
	}

	@Override
	public int compareTo(CategoryItem o) {
		
		return(getFullName().compareTo(o.getFullName()));
			
	}




}
