package app.managedBean.test;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import app.util.ThreadMonitorWinamp;

@ManagedBean
@ViewScoped
public class PushBean {
	ThreadMonitorWinamp t1 = new ThreadMonitorWinamp();
	
	public PushBean() {

		t1.start();

	}

	public void send() {
		
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();
		pushContext.push("/notifications", new FacesMessage("I'm LordGift", String.valueOf(t1.getPlayingMusic())));
	}
}
