package beans.fileSelect;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@RequestScoped
@Named
public class FileSelectBean implements Serializable {

/*
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
								
		
	}
	
	@PostConstruct
	void Init() {
	
		getStoredLocation();
		//UIInput locationComponent = (UIInput) context.getViewRoot().findComponent("j_idt6:j_idt7:location");		
		//String input = (String)locationComponent.getValue();
		
		fileRoots = FileUtilsLocal.getRootPaths();
	}

	protected void getStoredLocation() {
				
		location = "c:/";//context.getApplication().evaluateExpressionGet(context, "#{cc.attrs.location}", String.class);
	
		FileUtils f = FileUtilsFactory.create(location);
			
		if(f.getClass().getName().equals("virtualFile.FileUtilsLocal")) {
			
			FileUtilsLocal fl = (FileUtilsLocal)f;
			root = fl.getDrive().toUpperCase() + "/";
			
		} else {
			
			root = fileRoots.get(0);
		}			
	}
	
	protected void setStoredLocation() {
		
		ELContext elContext = context.getELContext();
		ValueExpression valueExpression = context.getApplication().getExpressionFactory()
		    .createValueExpression(elContext, "#{cc.attrs.location}", String.class);

		valueExpression.setValue(elContext, location);
		
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
		
		setStoredLocation();
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		
		this.location = location;	
	}
	
	public void setFtpLocation() {
	
		LocationRemote l = new LocationRemote(ftpAdress, username, password); 
		
		location = l.getLocation();
	
		setStoredLocation();
	}

	public String getSelectedFile() {
		return("");
		//return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		
		FileUtils f = FileUtilsFactory.create(location);
		
		if(selectedFile.equals("..")) {
						
			f.moveUp();
			
			location = f.getLocation();
			
		} else if(selectedFile.endsWith("/")) {
		
			f.moveDown(selectedFile);
			
			location = f.getLocation();		
		}
		
		setStoredLocation();
		
	}

*/

	
}
