package app.managedBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.component.picklist.PickList;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import com.qotsa.exception.InvalidHandle;
import com.qotsa.exception.InvalidParameter;
import com.qotsa.jni.controller.WinampController;

import app.util.FileUtils;
import app.util.WinampUtils;

@ManagedBean
@SessionScoped
public class DjController {
	private static final Logger log = Logger.getLogger(DjController.class);
	
	private String textStatus;
	private String playingMusic;
	private String promptTextHost;
	private String hostAddress;
	


	// Modifier this class (DualListModel) ;
	private DualListModel<String> songs;

	public DjController() {
		//start winamp
		WinampUtils.playerControl("run");
		
		//preparing DualListModel
		List<String> sourceSong = new ArrayList<String>();
		List<String> targetSong = FileUtils.getInstance().getMusicListFromDirectory();
		
		songs = new DualListModel<String>(sourceSong, targetSong);

		promptTextHost = "Please connect your music player to : ";
	}

	public DualListModel<String> getSongs() {
		return songs;
	}
	
	public void setSongs(DualListModel<String> songs) {
		this.songs = songs;
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
            msg.setSummary("Items Transferred");  
            msg.setDetail(builder.toString());  
            
            FacesContext.getCurrentInstance().addMessage(null, msg);    	
    	}
    	
        log.debug("Quit onTransfer");
    } 	
	
	public String getTextStatus() {
		return textStatus;
	}

	public void setTextStatus(String textStatus) {
		this.textStatus = textStatus;
	}

	public String getPromptTextHost() {
		return promptTextHost;
	}

	public String getHostAddress() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		String serverIP = request.getLocalAddr();
		
		return (!"".equals(serverIP) && serverIP != null) ? serverIP : "&lt;cannot get server's IP&gt;";
	}

	public String getPlayingMusic() {
		try {
			playingMusic = WinampController.getFileNamePlaying();
		} catch (InvalidHandle e) {
			log.error("Error in getPlayingMusic", e);
		}
		return playingMusic;
	}

	public void setPlayingMusic(String playingMusic) {
		this.playingMusic = playingMusic;
	}	
	
	public void playerControl(String clickedButton) {
		textStatus = WinampUtils.playerControl(clickedButton);
	}
	
    public void handleFileUpload(FileUploadEvent event) {
		log.debug("Enter handleFileUpload");
		try {
			// must encoded on xhtml page before
			String fileName = event.getFile().getFileName();
			InputStream inputStream = event.getFile().getInputstream();
			
			FileUtils.getInstance().copyFile(fileName, inputStream);

			//show message dialog to uploader
			FacesMessage msg = new FacesMessage("Success! ", fileName + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (IOException e) {
			log.error("Error in handleFileUpload", e);
		} finally {
			log.debug("Quit handleFileUpload");
		}
	}
}
