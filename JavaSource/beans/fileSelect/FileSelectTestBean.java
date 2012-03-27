package beans.fileSelect;

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named
public class FileSelectTestBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String location;

	public FileSelectTestBean() {
		
		location = "c:/";
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
}
