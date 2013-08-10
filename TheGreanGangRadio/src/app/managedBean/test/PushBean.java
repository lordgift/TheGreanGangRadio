package app.managedBean.test;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import app.util.ThreadMonitorWinamp;

@ManagedBean
@ViewScoped
public class PushBean {

	public void send() {
		
		PushContext pushContext = PushContextFactory.getDefault().getPushContext();
		pushContext.push("/notifications", new FacesMessage("I'm LordGift", "testing"));
	}
	
	public void getMsg() {
		FacesMessage msg = new FacesMessage();
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		msg.setSummary("TEST");
		msg.setDetail("Detail");

//		FacesContext.getCurrentInstance().addMessage(null, msg);
		
	    FacesContext context = FacesContext.getCurrentInstance();  
        context.addMessage(null, msg);  
	}
}
