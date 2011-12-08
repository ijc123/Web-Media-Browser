package database;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.ibatis.session.SqlSession;

@Stateless
public class SettingsEJB {
	
	public SettingsItem getSettings() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		SettingsItem settings = (SettingsItem) session.selectOne("database.SettingsMapper.getSettings");
			
		@SuppressWarnings("unchecked")
		List<MediaLocationItem> mediaLocations = 
				(List<MediaLocationItem>) session.selectList("database.MediaLocationMapper.getMediaLocations");
		
		settings.setMediaLocations(mediaLocations);
		
		session.close();
		
		return(settings);		
	}
	
	public void setSettings(final SettingsItem settings) {
		
		//SqlSession session = MyBatis.getSession().openSession(ExecutorType.BATCH);
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.update("database.SettingsMapper.setSettings", settings);
		session.delete("database.MediaLocationMapper.deleteMediaLocations");
		
		List<MediaLocationItem> mediaLocation = settings.getMediaLocations();
		
		for(int i = 0; i < mediaLocation.size(); i++) {
		
			session.insert("database.MediaLocationMapper.setMediaLocations", mediaLocation.get(i));
		}
			
		session.close();
	}
	
	
}
