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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SelectableDataModel;
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
public class DjController implements ServletContextListener, SelectableDataModel<String> {
	private static final Logger log = Logger.getLogger(DjController.class);

	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	ServletContext context = request.getSession().getServletContext();
	PushContext pushContext = PushContextFactory.getDefault().getPushContext();

	private String playingImage;
	private String playingMusic;
	private String promptTextHost;
	private String shareUrl;
	private String remoteAddress;
	private boolean playBooleanButton;
	private boolean stopBooleanButton;
	private List<Music> playlist;
	private List<Music> allMusic;
	private Music selected;
	private List<Music> filtered;

	private ThreadMonitorWinamp threadMonitorWinamp;

	public DjController() {

		threadMonitorWinamp = new ThreadMonitorWinamp();
		threadMonitorWinamp.start();

		// WinampUtils.clearWinampPlayList();

		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			shareUrl = "http://" + hostAddress + ":7810/TheGreanGangRadio/";
			context.setAttribute(Constants.SERVLETCONTEXT_HOST_ADDRESS, (String) hostAddress);
		} catch (UnknownHostException e) {
			log.error("Error in getHostAddress", e);
		}
		remoteAddress = request.getRemoteAddr();

		List<Music> list = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
		if(list == null) {
			playlist = new ArrayList<Music>();
			context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlist);
		}
		allMusic = FileUtils.getInstance().getMusicListFromDirectory();

		promptTextHost = "For users : ";
	}

	/*========	Music Table	========*/
	public List<Music> getAllMusic() {
		return allMusic;
	}

	public void setAllMusic(List<Music> allMusic) {
		this.allMusic = allMusic;
	}

	public Music getSelected() {
		return selected;
	}

	public void setSelected(Music selected) {
		this.selected = selected;
	}
	
	public List<Music> getFiltered() {
		return filtered;
	}

	public void setFiltered(List<Music> filtered) {
		this.filtered = filtered;
	}
	
	public List<Music> getPlaylist() {
		playlist = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
		return playlist;
	}

	public void setPlaylist(List<Music> playlist) {
		context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlist);
		this.playlist = playlist;
	}
	
	public void onRowSelect(Music music) {
		log.debug("Enter onRowSelect");
		
		/* no use selected row */
//		if(selected == null) {
			selected = music;
//		}
		
		allMusic.remove(selected);
		WinampUtils.appendFileToPlaylist(selected.getMusicName());
		selected.setRequestBy(NetworkUtils.getInstance().getHostNameByCommandLine(request.getRemoteAddr()));
		playlist.add(selected);

		context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlist);

		log.debug(selected.getMusicName() + " by " + selected.getRequestBy());

		FacesMessage msg = new FacesMessage();
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		msg.setSummary("Music Added by " + NetworkUtils.getInstance().managingSessionNetworkDetail().getHostName());
		msg.setDetail(selected.getMusicName() + " by " + selected.getRequestBy());

		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		pushContext.push(Constants.CHANNEL_REFRESH_ALLMUSIC_TABLE, Constants.STRING_VALUE_1);
		pushContext.push(Constants.CHANNEL_REFRESH_PLAYLIST_TABLE, Constants.STRING_VALUE_1);
		
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
				allMusic.add(music);
				allMusic = FileUtils.getInstance().checkDuplicateListElement(allMusic, music);
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Uploaded!", fileName);
			} else {
				msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Already Exist!", fileName);
			}
			// show message dialog to uploader
			FacesContext.getCurrentInstance().addMessage(null, msg);

			pushContext.push(Constants.CHANNEL_REFRESH_ALLMUSIC_TABLE, Constants.STRING_VALUE_1);

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

	/**
	 * remove music from playlist when it's playing
	 */
	public void removePlayingMusic() {
		Music equalsMusic = null;
		String fileNamePlaying = null;
		for (Music music : playlist) {
			fileNamePlaying = WinampUtils.getFileNamePlaying();
			if (music.getMusicName().equals(fileNamePlaying)) {
				equalsMusic = music;
			}
		}
		playlist.remove(equalsMusic);

		context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlist);
		
		pushContext.push(Constants.CHANNEL_REFRESH_PLAYLIST_TABLE, Constants.STRING_VALUE_1);
	}
	
	public String getPlayingImage() {
		try {
			if (WinampController.getStatus() == WinampController.PLAYING) {
				playingImage = Constants.IMAGESOURCE_BABY_DANCE_GIF;
			} else {
				playingImage = Constants.IMAGESOURCE_BABY_DANCE_JPG;
			}

			// push to client(user)
			pushContext.push(Constants.CHANNEL_PLAYING_IMAGE, "/TheGreanGangRadio" + playingImage);
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
	
	public String getRemoteAddress() {		
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

//	public void refreshDirectory() {
//		List<String> musics = FileUtils.getInstance().getMusicListFromDirectory();
//		songs.setTarget(musics);
//
//		context.setAttribute(Constants.SERVLETCONTEXT_DUAL_LIST_MODEL_SONGS, songs);
//		pushContext.push(Constants.CHANNEL_REFRESH_PICKLIST, Constants.STRING_VALUE_1);
//	}
	
	public String getShareUrl() {
		return shareUrl;
	}
	
	public String getPromptTextHost() {
		return promptTextHost;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			WinampController.exit();
		} catch (InvalidHandle e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		threadMonitorWinamp.interrupt();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

	}

	@Override
	public String getRowKey(String object) {
		log.debug("getRowKey = " + object);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRowData(String rowKey) {
		log.debug("getRowData = " + rowKey);
		// TODO Auto-generated method stub
		return null;
	}
}
