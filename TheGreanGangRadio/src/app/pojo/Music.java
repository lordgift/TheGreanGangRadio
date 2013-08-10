package app.pojo;

public class Music {
	private String musicName;
	private String requestBy;

	public Music(String musicName, String requestBy) {
		this.musicName = musicName;
		this.requestBy = requestBy;
	}
	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}
}
