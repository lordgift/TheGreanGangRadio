package app.util;

public interface Constants {
	/* channel for using primepush */
	public static final String CHANNEL_CHANGING_MUSIC = "/changingMusic";
	public static final String CHANNEL_PLAYING_IMAGE = "/playingImage";
	public static final String CHANNEL_REFRESH_ALLMUSIC_TABLE = "/refreshAllmusicTable";
	public static final String CHANNEL_REFRESH_PLAYLIST_TABLE = "/refreshPlaylistTable";
	public static final String CHANNEL_SERVER_MESSAGE = "/serverMessage";
	
	/* servletContext attribute */
	public static final String SERVLETCONTEXT_HOST_ADDRESS = "hostAddress";
	public static final String SERVLETCONTEXT_PLAYLIST = "playlistApplication";
	public static final String SERVLETCONTEXT_ALLMUSIC = "allMusicApplication";
	
	/* session attribute */
	public static final String SESSION_REMOTE_NETWORK_DETAIL = "networkDetail";
	
	/* hard code string*/
	public static final String IP_LOCALHOST = "127.0.0.1";
	
	public static final String PROTOCOL_HTTP = "http://";
	public static final String PORT_SHOUTCAST = ":8000";
	public static final String PORT_JBOSS = ":7810";
	public static final String CONTEXT_ROOT = "/On-The-Job_Playground";
	
	public static final String PAGE_LANDING = "./index.xhtml";
	public static final String PAGE_USERS = "/indexUser.xhtml";
	public static final String PAGE_DJ = "/indexDJ.xhtml";
	
	public static final String IMAGESOURCE_BABY_DANCE_JPG = "/img/baby-dance.jpg";
	public static final String IMAGESOURCE_BABY_DANCE_GIF = "/img/baby-dance.gif";
	
	public static final String BLANK = "";
	
}
