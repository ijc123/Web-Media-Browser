package beans;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import database.MediaLocationItem;
import database.MediaEJB;
import database.SettingsEJB;


@ViewScoped
@Named
public class SettingsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private SettingsEJB settingsEJB;
	
	@Inject
	private MediaEJB mediaEJB;
		
	private DataModel<MediaLocationItem> dataModel;
	private database.SettingsItem settings;

	@SuppressWarnings("unused")
	@PostConstruct
	private void Init() {

		settings = settingsEJB.getSettings();		

	}

	public database.SettingsItem getSettings() {

		return(settings);

	}

	public void synchronize() {
			
		//System.out.println(settings.getPreviewRootDirectory());
		
		//mediaEJB.synchronize(settings);
		
		video.FrameGrabber frameGrabber = new video.FrameGrabber();
		
		try {
			frameGrabber.start("file:///j:/Girls/Barbamiska/Barbamiska,%20Chinita,%20Angel%20Rivas,%20Lilo%20-%20Creampie%20Orgy%20%2312.avi", 320, "G:/preview/fapmap/grey.avi", 60);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
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
