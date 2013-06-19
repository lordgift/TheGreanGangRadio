package app.managedBean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

@ManagedBean
@ViewScoped
public class PushBean {	

	public void send() {
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();
		pushContext.push("/notifications", new FacesMessage("I'm LordGift", "I'm developer"));
	}
}
