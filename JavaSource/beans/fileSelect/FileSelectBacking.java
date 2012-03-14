package beans.fileSelect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;

import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import virtualFile.FileUtilsLocal;

@FacesComponent("fileSelect")
public class FileSelectBacking extends UIInput implements NamingContainer {

/*
	private String location;

	private String ftpAdress;
	private String username;
	private String password;
	//private String selectedFile;

	private String root;
	private List<String> fileRoots;

	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		location = (String)getAttributes().get("location");

		FileUtils f = FileUtilsFactory.create(location);

		fileRoots = FileUtilsLocal.getRootPaths();

		if(f.getClass().getName().equals("virtualFile.FileUtilsLocal")) {

			FileUtilsLocal fl = (FileUtilsLocal)f;
			root = fl.getDrive().toUpperCase() + "/";

		} else {

			root = fileRoots.get(0);
		}

		ftpAdress = "ftp://";

		UIInput component = (UIInput) findComponent("location");
		component.setValue(location);

		component = (UIInput) findComponent("selectDrive");
		component.setValue(root);

		component = (UIInput) findComponent("ftpAdress");
		component.setValue(ftpAdress);

		component = (UIInput) findComponent("username");
		component.setValue(username);

		component = (UIInput) findComponent("password");
		component.setValue(password);

		UISelectItems select = (UISelectItems) findComponent("drives");
		select.setValue(fileRoots);

		select = (UISelectItems) findComponent("fileListing");
		select.setValue(getFileListing());


	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] values = new Object[7];

		values[0] = super.saveState(context);
		values[1] = location;
		values[2] = ftpAdress;
		values[3] = username;
		values[4] = password;
		values[5] = root;
		values[6] = fileRoots;

		return(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[])state;

		super.restoreState(context, values[0]);
		location = (String)values[1];
		ftpAdress = (String)values[2];
		username = (String)values[3];
		password = (String)values[4];
		root = (String)values[5];
		fileRoots = (List<String>)values[6];

	}

	@Override
	public String getFamily() { return "javax.faces.NamingContainer"; }

	public String getFtpAdress() {
		return ftpAdress;
	}

	public void setFtpAdress(String ftpAdress) {
		this.ftpAdress = ftpAdress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public List<String> getFileRoots() {

		return(fileRoots);

	}

	public List<String> getFileListing() {

		List<String> fileListing = new ArrayList<String>();

		if(location.isEmpty()) return(fileListing);

		FileUtils f = FileUtilsFactory.create(location);

		ArrayList<FileInfo> directory = new ArrayList<FileInfo>();

		try {
			f.getDirectoryContents(directory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fileListing.add("..");

		for(int i = 0; i < directory.size(); i++) {

			fileListing.add(directory.get(i).getName() + "/");
		}

		return(fileListing);
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {

		this.root = root;
		location = root;

		UIInput component = (UIInput) findComponent("selectDrive");
		component.setValue(root);

		component = (UIInput) findComponent("location");
		component.setValue(location);

		UISelectItems select = (UISelectItems) findComponent("fileListing");
		select.setValue(getFileListing());

	}

	public void setFtpLocation() {

		UIInput component = (UIInput) findComponent("ftpAdress");
		ftpAdress = (String)component.getValue();

		component = (UIInput) findComponent("username");
		username = (String)component.getValue();

		if(username.isEmpty()) username = "anonymous";

		component = (UIInput) findComponent("password");
		password = (String)component.getValue();

		if(password.isEmpty()) password = "anonymous";

		LocationRemote l = new LocationRemote(ftpAdress, username, password);

		component = (UIInput) findComponent("location");

		location = l.getLocation();
		component.setValue(location);

		UISelectItems select = (UISelectItems) findComponent("fileListing");
		select.setValue(getFileListing());

	}

	public String getSelectedFile() {
		return("");
		//return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {

		//this.selectedFile = selectedFile;

		UIInput component = (UIInput) findComponent("location");
		location = (String)component.getValue();

		FileUtils f = FileUtilsFactory.create(location);

		if(selectedFile.equals("..")) {

			f.moveUp();

			location = f.getLocation();

		} else if(selectedFile.endsWith("/")) {

			f.moveDown(selectedFile);

			location = f.getLocation();
		}

		component.setValue(location);

		UISelectItems select = (UISelectItems) findComponent("fileListing");
		select.setValue(getFileListing());
	}
*/

}
