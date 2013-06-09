package app.managedBean;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import app.util.WinampUtils;

import com.qotsa.exception.InvalidHandle;
import com.qotsa.jni.controller.WinampController;

@ManagedBean(name = "winampBean")
@RequestScoped
public class WinampBean {
	String textStatus;
	String prompt;

	public WinampBean() {
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

}
