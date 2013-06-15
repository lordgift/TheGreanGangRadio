package app.managedBean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

import app.util.FileUtils;

@ManagedBean(name = "userController")
@RequestScoped
public class UserController {
	private String fileName;

    public void handleFileUpload(FileUploadEvent event) {
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
	}

	public String getFileName() {
		
		List<String> fileList = FileUtils.getInstance().getMusicListFromDirectory(FileUtils.ABSOLUTEPATH_MUSIC);
		
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
                    
