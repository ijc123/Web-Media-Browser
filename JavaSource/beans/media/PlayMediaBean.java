package beans.media;

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named
public class PlayMediaBean extends Taggable implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getFileName() {
		
		return(media.getFileName());
	}
	
}
