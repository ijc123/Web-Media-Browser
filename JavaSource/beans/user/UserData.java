package beans.user;

import video.HTTPLiveStreaming2;
import database.UserItem;

// per user data
public class UserData {

	private UserItem userItem;
	private HTTPLiveStreaming2 httpLiveTranscoder;
	
	public UserData(UserItem userItem) {
		
		this.userItem = userItem;
		httpLiveTranscoder = new HTTPLiveStreaming2();
	}

	public UserItem getUserItem() {
		return userItem;
	}

	public void setUserItem(UserItem userItem) {
		this.userItem = userItem;
	}
		
	public HTTPLiveStreaming2 getHttpLiveTranscoder() {
		return httpLiveTranscoder;
	}
	
}
