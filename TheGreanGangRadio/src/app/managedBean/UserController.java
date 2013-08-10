package app.managedBean;

import java.io.IOException;
import java.io.InputStream;
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
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import com.qotsa.exception.InvalidHandle;
import com.qotsa.jni.controller.WinampController;

import app.pojo.Music;
import app.util.Constants;
import app.util.FileUtils;
import app.util.NetworkUtils;
import app.util.WinampUtils;

@ManagedBean
@SessionScoped
public class UserController {
	private static final Logger log = Logger.getLogger(UserController.class);
	
	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	ServletContext context = request.getSession().getServletContext();
	PushContext pushContext = PushContextFactory.getDefault().getPushContext();
	
	private String playingImage;
	private String playingMusic;
	private String promptTextHost;
	private String streamingUrl;
	private String remoteAddress;
	private List<Music> playlist;
	private List<Music> allMusic;
	private Music selected;
	private List<Music> filtered;

	public UserController() {
		
		String hostAddress = (String) context.getAttribute(Constants.SERVLETCONTEXT_HOST_ADDRESS);
		streamingUrl = "http://"+hostAddress+":8000/";

		remoteAddress = request.getRemoteAddr();
		
		List<Music> list = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
		if(list == null) {
			playlist = new ArrayList<Music>();
			context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, playlist);
		}
		allMusic = FileUtils.getInstance().getMusicListFromDirectory();
		
		promptTextHost = "For external player : ";
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
	
	public String getStreamingUrl() {
		return streamingUrl;
	}
	
	public String getPromptTextHost() {
		return promptTextHost;
	}
	
}
