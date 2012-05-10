package beans.user;

import video.HTTPLiveStreaming;
import database.UserItem;

// per user data
public class UserData {

	private UserItem userItem;
	private HTTPLiveStreaming httpLiveTranscoder;
	
	public UserData(UserItem userItem) {
		
		this.userItem = userItem;
		httpLiveTranscoder = new HTTPLiveStreaming();
	}

	public UserItem getUserItem() {
		return userItem;
	}

	public void setUserItem(UserItem userItem) {
		this.userItem = userItem;
	}
		
	public HTTPLiveStreaming getHttpLiveTranscoder() {
		return httpLiveTranscoder;
	}
	
}
