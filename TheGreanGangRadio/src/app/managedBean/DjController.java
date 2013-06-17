package app.managedBean;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import app.util.FileUtils;
import app.util.WinampUtils;

@ManagedBean(name = "djController")
@RequestScoped
public class DjController {
	private static final Logger log = Logger.getLogger(DjController.class);
	
	String textStatus;
	String prompt;

	public DjController() {
		this.prompt = "Winamp : ";
	}

	public String getTextStatus() {
		return textStatus;
	}

	public void setTextStatus(String textStatus) {
		this.textStatus = textStatus;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public void play() {
		textStatus = WinampUtils.playerControl("play");
	}
	public void pause() {
		textStatus = WinampUtils.playerControl("pause");
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
