package database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import utils.ImageUtil;
import utils.MapArgument;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import beans.user.LoginBean;


@Stateless
public class MediaEJB {
	
	@Inject
	SettingsEJB settingsEJB;
		
	@Inject
	TagEJB tagEJB;
	
	@Inject
	LoginBean loginBean;
	
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getMediaByTagQuery(final List<String> tag) {
						
		if(tag.isEmpty()) return(new ArrayList<MediaItem>());
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(		
				"tags", tag,
				"size", tag.size(),
				"accessTypes", accessTypes
				);
		
		List<MediaItem> media  = (List<MediaItem>) session.selectList("database.MediaMapper.getMediaByTagQuery", map);
		
		session.close();
		
		return(media);		
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getMediaByFilenameQuery(String query, 
			java.sql.Timestamp fromTimestamp, java.sql.Timestamp toTimestamp,
			Integer minVersion, Integer maxVersion,
			List<String> excludeTypes) 
	{
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(query != null && query != "") {
			
			map.put("query", query);
		}
		
		if(fromTimestamp != null) {
					
			String timeStampString = fromTimestamp.toString();
			
			map.put("fromTimestamp", timeStampString);
		}
		
		if(toTimestamp != null) {
			
			String timeStampString = toTimestamp.toString();
			
			map.put("toTimestamp", timeStampString);
		}
	
		if(minVersion != null) {
			
			map.put("minVersion", minVersion);
		}
		
		if(maxVersion != null) {
			
			map.put("maxVersion", maxVersion);
		}
		
		// make sure to clone the accesstypes before editing them 
		List<String> accessTypes = 
				new ArrayList<String>(loginBean.getCurrentUser().getAccessTypes());
		
		if(excludeTypes != null) {
			
			accessTypes.removeAll(excludeTypes);		
		}
		
		map.put("accessTypes", accessTypes);
						
		List<MediaItem> result = (List<MediaItem>)session.selectList("database.MediaMapper.getMediaByFilenameQuery", map);		
		
		session.close();
		
		return(result);
			
	}
		
	public MediaItem getMediaByUri(String uri) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(		
				"uri", uri,
				"accessTypes", accessTypes
				);
		
		MediaItem media  = (MediaItem) session.selectOne("database.MediaMapper.getMediaByUri", map);
		
		session.close();
		
		return(media);	
		
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaItem> getAllMedia() {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		List<String> accessTypes = loginBean.getCurrentUser().getAccessTypes();
		
		Map<String, Object> map = MapArgument.create(		
				"accessTypes", accessTypes
				);
		
		List<MediaItem> media  = (List<MediaItem>) session.selectList("database.MediaMapper.getAllMedia", map);
		
		session.close();
		
		return(media);	
		
	}
	
	
	public void updateMedia(List<MediaItem> mediaItem) {
		
		for(int i = 0; i < mediaItem.size(); i++) {
			
			updateMedia(mediaItem.get(i));
		}
		
	}
	
	
	// potentially modifies mediaItem to it's updated state
	@SuppressWarnings("unchecked")
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
		List<TagItem> mediaTagItems = tagEJB.getTagByName(mediaTagNames);
		
		mediaTagItems = tagEJB.addLinkedTags(mediaTagItems);			
		
		MediaItem oldMediaItem = getMediaByUri(mediaItem.getUri());
		
		ArrayList<String> removedTags = new ArrayList<String>();
		
		if(oldMediaItem != null) {
					
			removedTags = (ArrayList<String>)oldMediaItem.getTagNames().clone();		
				
			removedTags.removeAll(mediaTagNames);
		}
		
		mediaTagNames.clear();
	
		for(int i = 0; i < mediaTagItems.size(); i++) {
			
			TagItem tag = mediaTagItems.get(i);
			
			if(!removedTags.contains(tag.getName())) {
			
				mediaTagNames.add(tag.getName());
			}
		}
				
		mediaItem.setTagNames(mediaTagNames);
			
		// update the mediaItem in the database
		
		setMedia(mediaItem, oldMediaItem);
		
	}
	
	
	private void setMedia(MediaItem newMediaItem, MediaItem oldMediaItem) {
		
		if(oldMediaItem != null) {
			
			if(oldMediaItem.equals(newMediaItem)) return;
			
			List<String> oldTags = oldMediaItem.getTagNames();
			List<String> newTags = newMediaItem.getTagNames();
							
			oldTags.removeAll(newTags);
				
			deleteMediaTags(newMediaItem.getUri(), oldTags);
			
			List<String> oldTypes = oldMediaItem.getTypeNames();
			List<String> newTypes = newMediaItem.getTypeNames();
			
			oldTypes.removeAll(newTypes);
			
			deleteMediaType(newMediaItem.getUri(), oldTypes);
			
			// update version
			newMediaItem.setVersion(oldMediaItem.getVersion() + 1);
			
		}
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		session.insert("database.MediaMapper.setMedia", newMediaItem);
		
		session.close();
		
		setMediaTags(newMediaItem);
		setMediaType(newMediaItem);
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
		
		updateMedia(result);
		
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
		
		updateMedia(result);
		
	}
		

	public void synchronize(SettingsItem settings) {

			
		try {
					
		// update last synchronized date to now
		java.util.Date curDate = Calendar.getInstance().getTime();
		Timestamp now = new Timestamp(curDate.getTime());
		
		settings.setSynchronized(now);
		
		List<MediaLocationItem> mediaLocation = settings.getMediaLocations();
		
		ArrayList<MediaItem> diskMedia = new ArrayList<MediaItem>();
		
		for(int i = 0; i < mediaLocation.size(); i++)  {
		
			MediaLocationItem m = mediaLocation.get(i);
			String typeName = m.getTypeName();
			
			FileUtils f = FileUtilsFactory.create(m.getLocation());
					
			f.getRecursiveMediaItems(diskMedia, m.isVideo(), m.isAudio(), m.isImages(), typeName);
		
		}
		
		//SqlSession session = MyBatis.getSession().openSession(); 
		SqlSession session = MyBatis.getSession().openSession(); 
							
		session.update("database.MediaMapper.dropTempTableNewMedia");
		session.update("database.MediaMapper.dropTempTableDiskMedia");
		session.update("database.MediaMapper.createTempTableDiskMedia");
		
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
		
		tagEJB.setTagUsedCounters();
		
		settingsEJB.setSettings(settings);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void setMediaVersions() {
				
		CategoryItem category = new CategoryItem();
		
		category.setTypeName("porn");
		category.setName("/pornstars");
		
		List<TagItem> pornstars = tagEJB.getTagsByCategory(category);
		
		Map<String, TagItem> pornstarHash = new HashMap<String, TagItem>();
		
		for(int i = 0; i < pornstars.size(); i++) {
			
			pornstarHash.put(pornstars.get(i).getName(), pornstars.get(i));
			
		}
		
		List<MediaItem> media = getAllMedia();
		
		for(int i = 0; i < media.size(); i++) {
		
			MediaItem mediaItem = media.get(i);
			
			@SuppressWarnings("unchecked")
			List<String> mediaTags = (List<String>)mediaItem.getTagNames().clone();
			
			if(mediaTags.isEmpty()) continue;
			
			for(int j = 0; j < mediaTags.size(); j++) {
				
				String tag = mediaTags.get(j);
				
				TagItem pornstar = pornstarHash.get(tag);
				
				if(pornstar != null) {
					
					mediaTags.remove(pornstar.getName());
					mediaTags.removeAll(pornstar.getLinkedTagNames());
				
				}
			}
		
			if(mediaTags.isEmpty()) {
				
				updateMedia(mediaItem);
				
			} else {
				
				updateMedia(mediaItem);	
				updateMedia(mediaItem);					
			}
			
		}		
		
	}

	public void storeMediaThumbnail(MediaItem media) {

		if(media == null || !media.isImage()) return;

		File imagefile = new File(media.getPath());
		FileInputStream fileInput = null;
		ByteArrayOutputStream byteOutput = null;

		ImageItem tagImage;

		try {

			fileInput = new FileInputStream(imagefile);
			byteOutput = new ByteArrayOutputStream();

			IOUtils.copy(fileInput, byteOutput);	

			ImageUtil imageUtil = new ImageUtil();
			
			tagImage = imageUtil.createThumbNail(byteOutput.toByteArray(), 200);
			tagImage.setOwner(media.getUri());

			SqlSession session = MyBatis.getSession().openSession(); 

			session.insert("database.MediaMapper.setMediaThumbnail", tagImage);

			session.close();

		} catch (Exception e) {

			e.printStackTrace();
			
		} finally {

			try {
				if(fileInput != null) fileInput.close();
				if(byteOutput != null) byteOutput.close();
			} catch (IOException ioe) {

			}

		}

	}
	
	public boolean hasMediaThumbnail(MediaItem media) {
		
		if(!media.isImage()) return(false);
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		Integer result = (Integer) session.selectOne("database.MediaMapper.hasMediaThumbnail", media.getUri());
		
		session.close();
		
		return(result != 0 ? true : false);
		
	}
	
	public ImageItem getMediaThumbnail(String uri) {
		
		SqlSession session = MyBatis.getSession().openSession(); 
		
		ImageItem tagImage = (ImageItem) session.selectOne("database.MediaMapper.getMediaThumbnail", uri);
		
		session.close();
		
		return(tagImage);
		
	}

	
	
	
}
