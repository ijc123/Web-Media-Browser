package beans.media;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PlayMediaBean extends Taggable {


	public String getFileName() {
		
		return(media.getFileName());
	}
	
}
