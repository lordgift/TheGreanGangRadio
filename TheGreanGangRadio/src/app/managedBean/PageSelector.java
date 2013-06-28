package app.managedBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import app.util.Constants;
import app.util.LogUtils;
import app.util.WinampUtils;

@ManagedBean
@RequestScoped
public class PageSelector {

	private static final Logger log = LogUtils.getLogger(PageSelector.class);
	
	private String page;
	
	
	public PageSelector() {
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		String remoteIP = httpServletRequest.getRemoteAddr();		
		
		log.debug("Entering IP="+remoteIP);
		
		if(Constants.IP_LOCALHOST.equals(remoteIP)) {
			//start winamp
			WinampUtils.playerControl(WinampUtils.PLAYER_ACTION_RUN);
			page = Constants.PAGE_DJ;
		} else {
			page = Constants.PAGE_USERS;
		}
	}

	public String getPage() {
		log.debug("forwarding to " + page);
		return page;
	}
}
