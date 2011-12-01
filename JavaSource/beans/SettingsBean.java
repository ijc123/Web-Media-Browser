package beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import database.MediaLocationItem;
import database.MediaTable;
import database.SettingsTable;

@ManagedBean
@ViewScoped
public class SettingsBean {

	@EJB
	private SettingsTable settingsTable;
	
	@EJB
	private MediaTable mediaTable;
		
	private DataModel<MediaLocationItem> dataModel;
	private database.SettingsItem settings;

	@SuppressWarnings("unused")
	@PostConstruct
	private void Init() {

		settings = settingsTable.getSettings();		

	}

	public database.SettingsItem getSettings() {

		return(settings);

	}

	public void synchronize() {
			
		mediaTable.synchronize(settings);
		
	}
	
	public DataModel<MediaLocationItem> getDataModel() {
			
		dataModel = new ListDataModel<MediaLocationItem>(settings.getMediaLocations());
		
		return(dataModel);
	}
	
	public void deleteMediaLocation() {
	        
		int index = dataModel.getRowIndex();
		
		List<MediaLocationItem> location = settings.getMediaLocations();
		
		location.remove(index);
		
		settings.setMediaLocations(location);
		
	}
	
	public void addMediaLocation() {
		
		List<MediaLocationItem> location = settings.getMediaLocations();
		
		MediaLocationItem newItem = new MediaLocationItem();
		
		location.add(newItem);
		
		settings.setMediaLocations(location);
		
	}

}
