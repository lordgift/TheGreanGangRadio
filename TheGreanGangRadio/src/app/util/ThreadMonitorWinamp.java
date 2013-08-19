package app.util;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import app.pojo.Music;

public class ThreadMonitorWinamp extends Thread {
	private Logger log = LogUtils.getLogger(ThreadMonitorWinamp.class);

	private String playingMusic;
	private String playedMusic;
	private String requester = "Unknown";

	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	ServletContext context = request.getSession().getServletContext();

	@Override
	public void run() {
		while (true) {
			try {
				playingMusic = WinampUtils.getFileNamePlaying();
				
				if (null != playedMusic && !playingMusic.equals(playedMusic)) {

					// requester="Unknown" when not found music in playlist from servletcontext
					List<Music> playlist = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
					if (playlist != null && !playlist.isEmpty()) {
						
						for (Music music : playlist) {
							if (music.getMusicName().equals(playingMusic)) {
								requester = music.getRequestBy();
								
								Music musicToShow = new Music(playingMusic, requester);

								PushContext pushContext = PushContextFactory.getDefault().getPushContext();
								pushContext.push(Constants.CHANNEL_CHANGING_MUSIC, musicToShow);
								
								log.debug("pushing to client");
							}
						}
					}
//					log.debug(playedMusic);
//					log.debug(playingMusic);
//					log.debug(requester);
				}
				playedMusic = playingMusic;

				sleep(3000);
			} catch (InterruptedException e) {
				log.error("Error in run", e);
				break;
			}
		}
	}

	public String getPlayingMusic() {
		return playingMusic;
	}
}
