package app.managedBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import app.util.FileUtils;
import app.util.ThreadMonitorWinamp;
import app.util.WinampUtils;

import com.qotsa.exception.InvalidHandle;
import com.qotsa.jni.controller.WinampController;

@ManagedBean
@SessionScoped
public class DjController {
	private static final Logger log = Logger.getLogger(DjController.class);
	
	private String playingImage;
	private String playingMusic;
	private String promptTextHost;
	private String hostAddress;
	private boolean playing;
	private boolean stopping;
	private ThreadMonitorWinamp threadMonitorWinamp;

	// Modifier this class (DualListModel) ;
	private DualListModel<String> songs;

	public DjController() {
		//start winamp
		WinampUtils.playerControl(WinampUtils.PLAYER_ACTION_RUN);
		
		threadMonitorWinamp = new ThreadMonitorWinamp();
		threadMonitorWinamp.start();
		
		WinampUtils.clearWinampPlayList();
		
		//preparing DualListModel
		List<String> sourceSongs = new ArrayList<String>();
		List<String> targetSongs = FileUtils.getInstance().getMusicListFromDirectory();
		
		songs = new DualListModel<String>(sourceSongs, targetSongs);

		promptTextHost = "Please connect your music player to : ";
	}

	public DualListModel<String> getSongs() {
		return songs;
	}
	
	public void setSongs(DualListModel<String> songs) {
		this.songs = songs;
	}
	
	public void refreshFileList() {		
		List<String> targetSongs = FileUtils.getInstance().getMusicListFromDirectory();
		songs.setTarget(targetSongs);
	}
	
	/**
	 * when transferring item(s) in PickList<BR />
	 * <p>
	 * Source - Winamp Playlist <BR/>
	 * Destination - Music Directory
	 * </p>
	 * 
	 * @param event object of transferring
	 */
    public void onTransfer(TransferEvent event) {  
    	log.debug("Enter onTransfer");
    	
    	if(event.isAdd()) {
    		//add=true is transfer source to destination
    		log.debug("removing Winamp Playlist(transferring source to destination)");
    		
    	} else {
    		//add=false is transfer destination to source ( can change to event.isRemove() )
    		log.debug("adding music(s) to Winamp Playlist(transferring destination to source)");
        	
            StringBuilder builder = new StringBuilder();  
            for(Object item : event.getItems()) {  
            	String fileName = (String) item;
                builder.append(fileName).append("<BR/>");
                WinampUtils.appendFileToPlaylist(fileName);
            }  
                 
            FacesMessage msg = new FacesMessage();  
            msg.setSeverity(FacesMessage.SEVERITY_INFO);  
            msg.setSummary("Music added to playlist");  
            msg.setDetail(builder.toString());  
            
            FacesContext.getCurrentInstance().addMessage(null, msg);    	
    	}
    	
        log.debug("Quit onTransfer");
    } 	
	
	public String getPromptTextHost() {
		return promptTextHost;
	}

	public String getHostAddress() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		String serverIP = request.getLocalAddr();
		
		return (!"".equals(serverIP) && serverIP != null) ? serverIP : "&lt;cannot get server's IP&gt;";
	}
	public void playOrPause() {
		if(playing) {
			WinampUtils.playerControl(WinampUtils.PLAYER_ACTION_PLAY);
		} else {
			WinampUtils.playerControl(WinampUtils.PLAYER_ACTION_PAUSE);
		}
	}

	public String getPlayingImage() {
		
		try {
			if(WinampController.getStatus() == WinampController.PLAYING) {
				playingImage = "../img/baby-dance.gif";
			} else {
				playingImage = "../img/baby-dance.jpg";
			}
		} catch (InvalidHandle e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return playingImage;
	}

	public void setPlayingImage(String playingImage) {
		this.playingImage = playingImage;
	}
	
	public String getPlayingMusic() {
		
		playingMusic = threadMonitorWinamp.getPlayingMusic();
		
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();
		pushContext.push("/notifications", playingMusic);
		
		return playingMusic;
	}

	public void setPlayingMusic(String playingMusic) {
		this.playingMusic = playingMusic;
	}	
	
	public void playerControl(String clickedButton) {
		WinampUtils.playerControl(clickedButton);
	}
	
	/**
	 * handle upload, copy file to main directory, check duplicate before update pickList 
	 * <BR/>and send message to uploader
	 * 
	 * @param event
	 */
    public void handleFileUpload(FileUploadEvent event) {
		log.debug("Enter handleFileUpload");
		try {
			// must encoded on xhtml page before
			String fileName = event.getFile().getFileName();
			InputStream inputStream = event.getFile().getInputstream();
			
			FileUtils.getInstance().copyFile(fileName, inputStream);

			//update list
			List<String> directory = songs.getTarget();
			boolean isAlready = false;
			for(String file : directory){
				if(file.equals(fileName)) isAlready = true;
			}
			FacesMessage msg;
			if( !isAlready ) {
				directory.add(fileName);
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Uploaded!", fileName);
			} else {
				msg = new FacesMessage(FacesMessage.SEVERITY_WARN,"Already Exist!", fileName);
			}
			songs.setTarget(directory);
			
			//show message dialog to uploader
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (IOException e) {
			log.error("Error in handleFileUpload", e);
		} finally {
			log.debug("Quit handleFileUpload");
		}
	}
    
	public boolean isPlaying() {
		if(stopping) playing = false;
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean isStopping() {
		if(playing) stopping = false;
		return stopping;
	}

	public void setStopping(boolean stopping) {
		this.stopping = stopping;
	}
		
}
