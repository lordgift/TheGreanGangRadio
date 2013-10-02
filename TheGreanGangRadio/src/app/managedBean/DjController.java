package app.managedBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import app.pojo.Music;
import app.util.Constants;
import app.util.FileUtils;
import app.util.NetworkUtils;
import app.util.ThreadMonitorWinamp;
import app.util.WinampUtils;

import com.qotsa.exception.InvalidHandle;
import com.qotsa.jni.controller.WinampController;

@ManagedBean
@SessionScoped
public class DjController {
	private static final Logger log = Logger.getLogger(DjController.class);

	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	ServletContext context = request.getServletContext();
	PushContext pushContext = PushContextFactory.getDefault().getPushContext();

	private String playingImage;
	private String playingMusic;
	private String playingRequester = "Unknown";
	private String promptWelcome;
	private String promptTextHost;
	private String shareUrl;
	private String streamingUrl;
	private String githubButtonUrl = "http://ghbtns.com/github-btn.html?user=lordgift&repo=TheGreanGangRadio&type=watch&count=true";
	private String remoteAddress;
	private String remoteHostName;
	private boolean playBooleanButton;
	private boolean stopBooleanButton;
//	private List<Music> playlist;
//	private List<Music> allMusic;
	private List<Music> filteredPlaylist;
	private List<Music> filteredAllMusic;
	private String serverMessage;

	public DjController() {
		ThreadMonitorWinamp.getInstance().start();

		// WinampUtils.clearWinampPlayList();
		
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			shareUrl = Constants.PROTOCOL_HTTP + hostAddress + Constants.PORT_JBOSS + Constants.CONTEXT_ROOT;
			streamingUrl = Constants.PROTOCOL_HTTP + hostAddress + Constants.PORT_SHOUTCAST;
			context.setAttribute(Constants.SERVLETCONTEXT_HOST_ADDRESS, (String) hostAddress);
		} catch (UnknownHostException e) {
			log.error("Error in getHostAddress", e);
		}
		remoteAddress = request.getRemoteAddr();
		remoteHostName = NetworkUtils.getInstance().getHostNameByCommandLine(remoteAddress);

		List<Music> playlistApplication = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
		if(playlistApplication == null) {
			playlistApplication = new ArrayList<Music>();
			context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlistApplication);
		}
		
		List<Music> allMusicApplication = FileUtils.getInstance().getMusicListFromDirectory();
		context.setAttribute(Constants.SERVLETCONTEXT_ALLMUSIC, allMusicApplication);

		promptWelcome = "Welcome, ";
		promptTextHost = "For users : ";
	}

	/*========	Music Table	========*/
	public List<Music> getFilteredPlaylist() {
		return filteredPlaylist;
	}

	public void setFilteredPlaylist(List<Music> filteredPlaylist) {
		this.filteredPlaylist = filteredPlaylist;
	}

	public List<Music> getFilteredAllMusic() {
		return filteredAllMusic;
	}

	public void setFilteredAllMusic(List<Music> filteredAllMusic) {
		this.filteredAllMusic = filteredAllMusic;
	}
	
	public void onRowSelect(Music music) {
		log.debug("Enter onRowSelect");
		
		
		if(filteredAllMusic != null)
			filteredAllMusic.remove(music);
		
		List<Music> allMusicApplication = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_ALLMUSIC);
		allMusicApplication.remove(music);
		context.setAttribute(Constants.SERVLETCONTEXT_ALLMUSIC, allMusicApplication);		
		
		WinampUtils.appendFileToPlaylist(music.getMusicName());
		
		music.setRequester(NetworkUtils.getAliasOfHostName(remoteHostName));
		
		List<Music> playlistApplication = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
		playlistApplication.add(music);
		context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlistApplication);

		log.info("Adding " + music.getMusicName() + " by " + music.getRequester());

		FacesMessage msg = new FacesMessage();
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		msg.setSummary("Music Added by " + NetworkUtils.getInstance().managingSessionNetworkDetail().getHostName());
		msg.setDetail(music.getMusicName() + " by " + music.getRequester());

		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		pushContext.push(Constants.CHANNEL_REFRESH_ALLMUSIC_TABLE, null);
		pushContext.push(Constants.CHANNEL_REFRESH_PLAYLIST_TABLE, null);
		
		log.debug("Quit onRowSelect");
	}
	/*========	Music Table	========*/
	/*========	Handle Upload	========*/
	/**
	 * handle upload, copy file to main directory, check duplicate before update
	 * pickList <BR/>
	 * and send message to uploader
	 * 
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		log.debug("Enter handleFileUpload");
		try {
			// must encoded on xhtml page before
			String fileName = event.getFile().getFileName();

			// update list
			List<Music> musicInDirectory = FileUtils.getInstance().getMusicListFromDirectory();
			boolean isAlready = false;
			for (Music music : musicInDirectory) {
				if (fileName.equals(music.getMusicName()))
					isAlready = true;
			}
			FacesMessage msg;
			if (!isAlready) {
				InputStream inputStream = event.getFile().getInputstream();
				FileUtils.getInstance().copyFile(fileName, inputStream);

				Music music = new Music(fileName, null);
				List<Music> allMusicApplication = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_ALLMUSIC);
				allMusicApplication.add(music);					
				allMusicApplication = FileUtils.getInstance().checkDuplicateListElement(allMusicApplication, music);
				context.setAttribute(Constants.SERVLETCONTEXT_ALLMUSIC, allMusicApplication);		
				
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Uploaded!", fileName);
			} else {
				msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Already Exist!", fileName);
			}
			// show message dialog to uploader
			FacesContext.getCurrentInstance().addMessage(null, msg);

			pushContext.push(Constants.CHANNEL_REFRESH_ALLMUSIC_TABLE, null);

		} catch (IOException e) {
			log.error("Error in handleFileUpload", e);
		} finally {
			log.debug("Quit handleFileUpload");
		}
	}
	/*========	Handle Upload	========*/
	/*========	Controller Panel	========*/
	public void playerControl(String clickedButton) {
		WinampUtils.playerControl(clickedButton);
	}

	public void playOrPause() {
		if (playBooleanButton) {
			WinampUtils.playerControl(WinampUtils.PLAYER_ACTION_PLAY);
		} else {
			WinampUtils.playerControl(WinampUtils.PLAYER_ACTION_PAUSE);
		}
	}

	public boolean isPlayBooleanButton() {
		if (stopBooleanButton)
			playBooleanButton = false;
		return playBooleanButton;
	}

	public void setPlayBooleanButton(boolean playBooleanButton) {
		this.playBooleanButton = playBooleanButton;
	}

	public boolean isStopBooleanButton() {
		if (playBooleanButton)
			stopBooleanButton = false;
		return stopBooleanButton;
	}

	public void setStopBooleanButton(boolean stopBooleanButton) {
		this.stopBooleanButton = stopBooleanButton;
	}
	/*========	Controller Panel	========*/

	public String getPlayingImage() {
		try {
			if (WinampController.getStatus() == WinampController.PLAYING) {
				playingImage = Constants.IMAGESOURCE_BABY_DANCE_GIF;
			} else {
				playingImage = Constants.IMAGESOURCE_BABY_DANCE_JPG;
			}

			// push to client(user)
			pushContext.push(Constants.CHANNEL_PLAYING_IMAGE, Constants.CONTEXT_ROOT + playingImage);
		} catch (InvalidHandle e) {
			log.error("Error in getPlayingImage", e);
		}
		return playingImage;
	}

	public void setPlayingImage(String playingImage) {
		this.playingImage = playingImage;
	}

	public String getPlayingMusic() {
		playingMusic = WinampUtils.getFileNamePlaying();
		return playingMusic;
	}

	public void setPlayingMusic(String playingMusic) {
		this.playingMusic = playingMusic;
	}
	
	public String getPlayingRequester() {
		return playingRequester;
	}

	public void setPlayingRequester(String playingRequester) {
		this.playingRequester = playingRequester;
	}

	public String getRemoteAddress() {		
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public void reloadDirectory() {
		log.debug("Enter reloadDirectory");

		List<Music> musics = FileUtils.getInstance().getMusicListFromDirectory();
		List<Music> allMusicApplication = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_ALLMUSIC);
		allMusicApplication.clear();
		allMusicApplication.addAll(musics);
		context.setAttribute(Constants.SERVLETCONTEXT_ALLMUSIC, allMusicApplication);
		
		pushContext.push(Constants.CHANNEL_REFRESH_ALLMUSIC_TABLE, null);
		log.debug("Exit reloadDirectory");
	}
	
	public void sendMessage() {
		pushContext.push(Constants.CHANNEL_SERVER_MESSAGE, serverMessage);
	}
	
	public String getServerMessage() {
		return serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

	public String getPromptWelcome() {
		return promptWelcome;
	}
	
	public String getRemoteHostName() {
		return NetworkUtils.getAliasOfHostName(remoteHostName);
	}
	
	public String getShareUrl() {
		return shareUrl;
	}
	
	public String getStreamingUrl() {
		return streamingUrl;
	}
	
	public String getPromptTextHost() {
		return promptTextHost;
	}
	
	public String getGithubButtonUrl() {
		return githubButtonUrl;
	}

	public void setGithubButtonUrl(String githubButtonUrl) {
		this.githubButtonUrl = githubButtonUrl;
	}
}
