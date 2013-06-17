package app.managedBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import app.util.FileUtils;

@ManagedBean(name = "userController")
@RequestScoped
public class UserController {
	private static final Logger log = Logger.getLogger(UserController.class);
	private String fileName;

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

	public String getFileName() {
		
		List<String> fileList = FileUtils.getInstance().getMusicListFromDirectory(FileUtils.ABSOLUTEPATH_THE_GREAN_GANG_RADIO);
		
		StringBuilder fileName = new StringBuilder();
		for(int i=0;i<fileList.size();++i)
			fileName.append(fileList.get(i));
		this.fileName = fileName.toString();
		
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
                    
