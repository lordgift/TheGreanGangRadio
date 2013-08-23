package app.pojo;

public class Music {
	private String musicName;
	private String requester;

	public Music(String musicName, String requestBy) {
		this.musicName = musicName;
		this.requester = requestBy;
	}
	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}
}
