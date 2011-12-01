package database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import fileUtils.FileUtils;

@Stateless
public class MediaTable {
	
	@EJB
	SettingsTable settingsTable;
		
	@EJB
	TagTable tagTable;
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getMediaByTagQuery(final List<String> tag) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("tags", tag);
		map.put("size", tag.size());
				
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<MediaItem> media  = (List<MediaItem>) session.selectList("database.MediaMapper.getMediaByTagQuery", map);
		
		session.close();
		
		return(media);		
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getMediaByFilenameQuery(String query, java.sql.Timestamp timeStamp) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		if(query != null && query != "") {
			
			map.put("query", query);
		}
		
		if(timeStamp != null) {
					
			String timeStampString = timeStamp.toString();
			
			map.put("timeStamp", timeStampString);
		}
	
		SqlSession session = MyBatis.getSession().openSession(); 
			
		List<MediaItem> result = (List<MediaItem>)session.selectList("database.MediaMapper.getMediaByFilenameQuery", map);		
		
		session.close();
		
		return(result);
			
	}
		
	public MediaItem getMediaByUri(String uri) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		MediaItem media  = (MediaItem) session.selectOne("database.MediaMapper.getMediaByUri", uri);
		
		session.close();
		
		return(media);	
		
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getAllMedia() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<MediaItem> media  = (List<MediaItem>) session.selectList("database.MediaMapper.getAllMedia");
		
		session.close();
		
		return(media);	
		
	}
	
	
	public void updateMedia(List<MediaItem> mediaItem) {
		
		for(int i = 0; i < mediaItem.size(); i++) {
			
			updateMedia(mediaItem.get(i));
		}
		
	}
	
	
	// potentially modifies mediaItem by adding linked tags
	public void updateMedia(MediaItem mediaItem) {
		
		// remove potential duplicate tags from mediaItem
		ArrayList<String> mediaTagNames = mediaItem.getTagNames();
				
		HashSet<String> uniqueTagNamesHash = new HashSet<String>(mediaTagNames);
						
		Object[] temp1 = uniqueTagNamesHash.toArray();
		
		String[] temp2 = Arrays.copyOf(temp1, temp1.length, String[].class);
		
		mediaTagNames = new ArrayList<String>(Arrays.asList(temp2));
		
		// Add linked tags to the mediaItem
		// If tags are removed from the old media item
		// do not re-introduce them as linked tags 		
		MediaItem oldMediaItem = getMediaByUri(mediaItem.getUri());
		
		ArrayList<String> removedTags = oldMediaItem.getTagNames();		
				
		removedTags.removeAll(mediaTagNames);
				
		List<TagItem> mediaTagItems = tagTable.getTagByName(mediaTagNames);
		
		mediaTagItems = tagTable.addLinkedTags(mediaTagItems);			
		
		mediaTagNames.clear();
		
		for(int i = 0; i < mediaTagItems.size(); i++) {
			
			TagItem tag = mediaTagItems.get(i);
			
			if(!removedTags.contains(tag.getName())) {
			
				mediaTagNames.add(tag.getName());
			}
		}
		
		mediaItem.setTagNames(mediaTagNames);
			
		// update the mediaItem
		
		setMedia(mediaItem);
		
	}
	
	
	private void setMedia(final MediaItem newMediaItem) {
		
		MediaItem oldMediaItem = getMediaByUri(newMediaItem.getUri());
		
		if(oldMediaItem != null) {
			
			List<String> oldTags = oldMediaItem.getTagNames();
			List<String> newTags = newMediaItem.getTagNames();
							
			oldTags.removeAll(newTags);
				
			deleteMediaTags(newMediaItem.getUri(), oldTags);
			
			List<String> oldTypes = oldMediaItem.getTypeNames();
			List<String> newTypes = newMediaItem.getTypeNames();
			
			oldTypes.removeAll(newTypes);
			
			deleteMediaType(newMediaItem.getUri(), oldTypes);
			
		}
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.insert("database.MediaMapper.setMedia", newMediaItem);
		
		session.close();
		
		setMediaTags(newMediaItem);
		setMediaType(newMediaItem);
	}
	
	private void setMedia(List<MediaItem> media) {
		
		for(int i = 0; i < media.size(); i++) {
		
			setMedia(media.get(i));
		}
				
	}
	
	private void setMediaTags(final MediaItem media) {
		
		List<String> tags = media.getTagNames();
		
		if(tags == null || tags.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
									
		for(int i = 0; i < tags.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("uri", media.getUri());
			map.put("tag", tags.get(i));
		
			session.insert("database.MediaMapper.setMediaTag", map);
			
		}
			
		session.close();
	}
	
	private void deleteMediaTags(String uri, final List<String> tags) {
		
		if(tags.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
					
		for(int i = 0; i < tags.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("uri", uri);
			map.put("tag", tags.get(i));
		
			session.insert("database.MediaMapper.deleteMediaTag", map);
			
		}
			
		session.close();
	}
	
	private void setMediaType(final MediaItem media) {
		
		List<String> type = media.getTypeNames();
		
		if(type == null || type.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(ExecutorType.BATCH); 
						
		for(int i = 0; i < type.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("uri", media.getUri());
			map.put("type", type.get(i));
		
			session.insert("database.MediaMapper.setMediaType", map);
			
		}	
	
		session.close();
	}
	
	private void deleteMediaType(String uri, final List<String> typeNames) {
		
		if(typeNames.isEmpty()) return;
		
		SqlSession session = MyBatis.getSession().openSession(); 
				
		for(int i = 0; i < typeNames.size(); i++) {
			
			Map<String, String> map = new HashMap<String, String>();
			
			map.put("uri", uri);
			map.put("type", typeNames.get(i));
		
			session.insert("database.MediaMapper.deleteMediaType", map);
			
		}
		
		session.close();
				
	}
	
	
	public void updateLinkedTags(final TagItem tag) {
		
		List<String> linkedTagNames = tag.getLinkedTagNames();
		
		if(linkedTagNames.isEmpty()) return;
				
		ArrayList<String> searchTag = new ArrayList<String>();
		searchTag.add(tag.getName());
		
		List<MediaItem> result = getMediaByTagQuery(searchTag);
		
		for(int i = 0; i < result.size(); i++) {
		
			ArrayList<String> tagNames = result.get(i).getTagNames();
					
			for(int j = 0; j < linkedTagNames.size(); j++) {
			
				String linkedTag = linkedTagNames.get(j);
				
				if(!tagNames.contains(linkedTag)) {
						
					tagNames.add(linkedTag);
				}		
			}					
		}
		
		setMedia(result);
		
	}
	
	// remove tag from the media database
	public void deleteTag(TagItem tag) {
		
		ArrayList<String> searchTags = new ArrayList<String>();
		searchTags.add(tag.getName());
		
		List<MediaItem> result = getMediaByTagQuery(searchTags);
				
		if(result.isEmpty()) return;
		
		for(int i = 0; i < result.size(); i++) {
			
			MediaItem media = result.get(i);
			
			media.getTagNames().remove(tag.getName());
			
		}
		
		setMedia(result);
		
	}
		

	public void synchronize(SettingsItem settings) {
								
		// update last synchronized date to now
		java.util.Date curDate = Calendar.getInstance().getTime();
		Timestamp now = new Timestamp(curDate.getTime());
		
		settings.setSynchronized(now);
		
		List<MediaLocationItem> mediaLocation = settings.getMediaLocations();
		
		ArrayList<MediaItem> diskMedia = new ArrayList<MediaItem>();
		
		for(int i = 0; i < mediaLocation.size(); i++)  {
		
			MediaLocationItem m = mediaLocation.get(i);
			String typeName = m.getTypeName();
			
			FileUtils f = new FileUtils(m.getLocation());
		
			f.getRecursiveMediaItems(diskMedia, m.isVideo(), m.isAudio(), m.isImages(), typeName);
		}
		
		//SqlSession session = MyBatis.getSession().openSession(); 
		SqlSession session = MyBatis.getSession().openSession(); 
					
		session.update("database.MediaMapper.dropTempTable");
		session.update("database.MediaMapper.createTempTable");
		
		for(int i = 0; i < diskMedia.size(); i++) {

			Map<String, String> map = new HashMap<String, String>();
			
			map.put("uri", diskMedia.get(i).getUri());
			map.put("fileName", diskMedia.get(i).getFileName());
			map.put("sizeBytes", diskMedia.get(i).getSizeBytes().toString());
			map.put("typeName", diskMedia.get(i).getTypeNames().get(0));
			
			session.insert("database.MediaMapper.setMediaTempTable", map);	

		}

		session.update("database.MediaMapper.synchronize");
			
		int nrMediaItems = (Integer)session.selectOne("database.MediaMapper.nrRowsInMedia");
				
		settings.setNrMediaItems(nrMediaItems);
		
		session.close();

		System.out.println("done synchronizing database, counting tag usage");
		
		tagTable.setTagUsedCounters();
		
		settingsTable.setSettings(settings);
	
	}
	
	public void Test() {
			
		List<String> tags = new ArrayList<String>();
		
		tags.add("jessica fiorentino");
		
		List<MediaItem> result = getMediaByTagQuery(tags);
		
		result = getMediaByFilenameQuery("jessica fiorentino", null);
		
		MediaItem item = getMediaByUri(result.get(0).getUri());
		
		long oldSize = item.getSizeBytes();
		
		item.setSizeBytes(0);
		
		setMedia(item);
		
		item = getMediaByUri(result.get(0).getUri());
		
		item.setSizeBytes(oldSize);
		
		setMedia(item);
	
		
	}
	
	
	
}
