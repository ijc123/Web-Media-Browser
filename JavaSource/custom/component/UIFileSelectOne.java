package custom.component;

import java.util.ArrayList;

import javax.faces.component.FacesComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;

@FacesComponent("custom.component.FileSelectOne")
public class UIFileSelectOne extends UISelectOne {
	
	private String rootDir;
	private ArrayList<String> directories = null;
	private ArrayList<String> files = null;
	private boolean hideFiles;
	private boolean hideDirectories;
	private int size;
	
	public UIFileSelectOne() {
		
		setRendererType("custom.component.fileSelectOne.FileSelectOne");
		hideFiles = false;
		hideDirectories = false;
		size = 5;
		rootDir = "";
	}

	@Override
	public Object saveState(FacesContext context) {
		
		Object[] values = new Object[7];
		
		values[0] = super.saveState(context);
		values[1] = directories;
		values[2] = files;
		values[3] = Boolean.valueOf(hideFiles);
		values[4] = Boolean.valueOf(hideDirectories);
		values[5] = Integer.valueOf(size);
		values[6] = rootDir;
		
		return(values);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {
		
		Object[] values = (Object[])state;
		
		super.restoreState(context, values[0]);
		directories = (ArrayList<String>)values[1];
		files = (ArrayList<String>)values[2];
		hideFiles = (Boolean)values[3];
		hideDirectories = (Boolean)values[4];
		size = (Integer)values[5];
		rootDir = (String)values[6];
		
	}
	
	

	public void setDirectories(ArrayList<String> directories) {
		this.directories = directories;
	}


	public ArrayList<String> getDirectories() {
		return directories;
	}


	public void setFiles(ArrayList<String> files) {
		this.files = files;
	}


	public ArrayList<String> getFiles() {
		return files;
	}
	
	public boolean isHideFiles() {
		return hideFiles;
	}

	public void setHideFiles(boolean hideFiles) {
		this.hideFiles = hideFiles;
	}

	public boolean isHideDirectories() {
		return hideDirectories;
	}

	public void setHideDirectories(boolean hideDirectories) {
		this.hideDirectories = hideDirectories;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setRootDir(String rootDir) {
		
		this.rootDir = rootDir;
	}
	
	public String getRootDir() {
	
		return(rootDir);
	
	}
}
