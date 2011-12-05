package beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import database.MediaLocationItem;
import database.MediaTable;
import database.SettingsTable;


@ViewScoped
@Named
public class SettingsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private SettingsTable settingsTable;
	
	@Inject
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
