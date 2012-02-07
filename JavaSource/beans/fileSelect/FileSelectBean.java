package beans.fileSelect;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import virtualFile.FileUtilsLocal;
import virtualFile.LocationRemote;

@ViewScoped
@Named
public class FileSelectBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String location;
	
	private String ftpAdress;
	private String username;
	private String password;
	//private String selectedFile;
	
	private String root;	
	private List<String> fileRoots;
		
	@Inject
	FacesContext context;
		
	public FileSelectBean() {
								
		fileRoots = FileUtilsLocal.getRootPaths();
		
		if(!fileRoots.isEmpty()) {
			
			if(fileRoots.contains("C:/")) {
				
				root = "C:/";
				location = "C:/";
			
			} else {
			
				root = fileRoots.get(0);
				location = fileRoots.get(0);
			}
			
		} else {
			
			root = "";
			location = "";
		}
		
		ftpAdress = "ftp://";
	}
	
	@PostConstruct
	void Init() {
	
		UIInput locationComponent = (UIInput) context.getViewRoot().findComponent("j_idt6:j_idt7:location");
		
		String input = (String)locationComponent.getValue();
		
		String blub = context.getApplication().evaluateExpressionGet(context, "#{cc.attrs.location}", String.class);
				
		int i = 0;
	
	}
	
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
		
		ArrayList<FileInfo> file = new ArrayList<FileInfo>();
		ArrayList<FileInfo> directory = new ArrayList<FileInfo>();
		
		try {
			f.getDirectoryContents(directory, file);
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
	}
	
	public void setFtpLocation() {
	
		LocationRemote l = new LocationRemote(ftpAdress, username, password); 
		
		location = l.getLocation();
	
	}

	public String getSelectedFile() {
		return("");
		//return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		
		//this.selectedFile = selectedFile;
		
		FileUtils f = FileUtilsFactory.create(location);
		
		if(selectedFile.equals("..")) {
						
			f.moveUp();
			
			location = f.getLocation();
			
		} else if(selectedFile.endsWith("/")) {
		
			f.moveDown(selectedFile);
			
			location = f.getLocation();		
		}
	}



	
}
