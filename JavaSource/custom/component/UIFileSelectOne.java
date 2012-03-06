package custom.component;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UISelectOne;

@FacesComponent("custom.component.FileSelectOne")
public class UIFileSelectOne extends UISelectOne {


	public UIFileSelectOne() {
		
		super();
		setRendererType("custom.component.fileSelectOne.FileSelectOne");
		
	}
	
	protected enum Properties {
		location,
		contents, 
		roots,
		rootDrive, 
		hideFiles, 
		hideDirectories, 
		size, 
		ftpServer, 
		ftpUsername, 
		ftpPassword,
		firstRender,
		ftpConnectMessage
	}

	// save/restoreState is only needed to make this component work
	// in a UIData table
/*	
	@Override
	public Object saveState(FacesContext context) {
	
		Object[] values = new Object[12];

		values[0] = super.saveState(context);
		values[1] = getLocation();
		values[2] = getContents();
		values[3] = getRoots();
		values[4] = getRootDrive();
		values[5] = Boolean.valueOf(isHideFiles());
		values[6] = Boolean.valueOf(isHideDirectories());
		values[7] = Integer.valueOf(getSize());
		values[8] = getFtpServer();
		values[9] = getFtpUsername();
		values[10] = getFtpPassword();
		values[11] = Boolean.valueOf(isFirstRender());

		return(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[]) state;

		super.restoreState(context, values[0]);
		setLocation((String)values[1]); 
		setContents((List<String>) values[2]);
		setRoots((List<String>) values[3]);
		setRootDrive((String) values[4]);
		setHideFiles((Boolean) values[5]);
		setHideDirectories((Boolean) values[6]);
		setSize((Integer) values[7]);
		setFtpServer((String) values[8]);
		setFtpUsername((String) values[9]);
		setFtpPassword((String) values[10]);
		setFirstRender((Boolean) values[11]);

	}
	
*/
		
	public java.lang.String getLocation() {
	
		String value = (String)getStateHelper().eval(Properties.location);
		return value;
	}
	
	public void setLocation(java.lang.String value) {
		
		getStateHelper().put(Properties.location, value);
		
		ValueExpression f = getValueExpression("location");
		
		if(f != null) {
		
			ELContext elContext = this.getFacesContext().getELContext();
		
			f.setValue(elContext, value);
		}
	}
		
	@SuppressWarnings("unchecked")
	public List<String> getContents() {
		List<String> value = (List<String>) getStateHelper().eval(Properties.contents, new ArrayList<String>());
		return value;
	}

	public void setContents(List<String> contents) {
		getStateHelper().put(Properties.contents, contents);
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoots() {
		List<String> value = (List<String>) getStateHelper().eval(Properties.roots, new ArrayList<String>());
		return value;
	}

	public void setRoots(List<String> roots) {
		getStateHelper().put(Properties.roots, roots);
	}
		
	public String getRootDrive() {
		String value = (String) getStateHelper().eval(Properties.rootDrive);
		return value;
	}

	public void setRootDrive(String rootDrive) {
		getStateHelper().put(Properties.rootDrive, rootDrive);
	}

	public boolean isHideFiles() {
		boolean value = (Boolean) getStateHelper().eval(Properties.hideFiles, Boolean.valueOf(false));
		return value;
	}

	public void setHideFiles(boolean hideFiles) {
		getStateHelper().put(Properties.hideFiles, hideFiles);
	}

	public boolean isHideDirectories() {
		boolean value = (Boolean) getStateHelper().eval(Properties.hideDirectories, Boolean.valueOf(false));
		return value;
	}

	public void setHideDirectories(boolean hideDirectories) {
		getStateHelper().put(Properties.hideDirectories, hideDirectories);
	}

	public int getSize() {

		int value = (Integer) getStateHelper().eval(Properties.size, Integer.valueOf(10));
		return value;
	}

	public void setSize(int size) {
		getStateHelper().put(Properties.size, size);
	}

	public String getFtpServer() {
		String value = (String) getStateHelper().eval(Properties.ftpServer, "ftp://");		
		return value;
	}

	public void setFtpServer(String ftpServer) {
		getStateHelper().put(Properties.ftpServer, ftpServer);
		
		ValueExpression f = getValueExpression("ftpServer");
		
		if(f != null) {
		
			ELContext elContext = this.getFacesContext().getELContext();
		
			f.setValue(elContext, ftpServer);
		}
	}
	
	public String getFtpUsername() {
		String value = (String) getStateHelper().eval(Properties.ftpUsername, "anonymous");
		return value;
	}

	public void setFtpUsername(String ftpUsername) {
		getStateHelper().put(Properties.ftpUsername, ftpUsername);
		
		ValueExpression f = getValueExpression("ftpUsername");
		
		if(f != null) {
		
			ELContext elContext = this.getFacesContext().getELContext();
		
			f.setValue(elContext, ftpUsername);
		}
	
	}
	
	public String getFtpPassword() {
		String value = (String) getStateHelper().eval(Properties.ftpPassword, "anonymous");
		return value;
	}

	public void setFtpPassword(String ftpPassword) {
		getStateHelper().put(Properties.ftpPassword, ftpPassword);
		
		ValueExpression f = getValueExpression("ftpPassword");
		
		if(f != null) {
		
			ELContext elContext = this.getFacesContext().getELContext();
		
			f.setValue(elContext, ftpPassword);
		}
	}
	
	public void setFirstRender(boolean firstRender) {
		getStateHelper().put(Properties.firstRender, firstRender);
	}

	public boolean isFirstRender() {
		boolean value = (Boolean) getStateHelper().eval(Properties.firstRender, Boolean.valueOf(true));
		return value;
	}

	public String getFtpConnectMessage() {
		
		String value = (String) getStateHelper().eval(Properties.ftpConnectMessage, "");
		return value;
		
	}
	
	public void setFtpConnectMessage(String message) {
		
		getStateHelper().put(Properties.ftpConnectMessage, message);
		
	}


}
