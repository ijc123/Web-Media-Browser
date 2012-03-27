package beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import database.MediaEJB;
import database.MediaLocationItem;
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
					
		mediaEJB.synchronize(settings);

/*	
		FrameGrabber frameGrabber = new FrameGrabber();
		ImagePreviewBuilder previewBuilder = new ImagePreviewBuilder();
		
		try {

			Location inputLocation = new Location("file:///j:/Girls/Mira/Mira%20-%20Pix%20And%20Video.mp4");
			String outputPath = "G:/previewtemp/dummy/";
			
			String outputFilename = outputPath + inputLocation.getFilename();
			
			Location outputLocation = new Location(outputFilename);
						
			//frameGrabber.start(inputLocation, 400, outputLocation, 60);
			previewBuilder.start(outputLocation);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
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
