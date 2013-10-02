package app.util;

import java.util.ArrayList;
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
	private Music musicDisplay;
	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	ServletContext context = request.getSession().getServletContext();
	PushContext pushContext = PushContextFactory.getDefault().getPushContext();
	
	private static final ThreadMonitorWinamp INSTANCE = new ThreadMonitorWinamp();
	private ThreadMonitorWinamp() {
		/* singleton for protected multithread */
	}
	
	public static ThreadMonitorWinamp getInstance() {
		return INSTANCE != null ? INSTANCE : new ThreadMonitorWinamp() ;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				playingMusic = WinampUtils.getFileNamePlaying();
				
				/* is music changed && check null of playedMusic for protect context.getAttribute is null*/
				if (playedMusic != null && playingMusic != null &&!playingMusic.equals(playedMusic)) {
					
					musicDisplay = new Music(playingMusic,"Unknown");
					
					List<Music> contextPlaylist = (List<Music>) context.getAttribute(Constants.SERVLETCONTEXT_PLAYLIST);
					List<Music> playlist = new ArrayList<Music>(contextPlaylist);
					
					if (playlist != null && !playlist.isEmpty()) {
						/* find & remove Playlist */
						for (Music music : playlist) {
							if (playingMusic.equals(music.getMusicName())) {
								contextPlaylist.remove(music);
								context.setAttribute(Constants.SERVLETCONTEXT_PLAYLIST, contextPlaylist);
								
								musicDisplay.setRequester(music.getRequester());
								
								/* for remove only first duplicated music*/
								break;
							}							
						}
					}
					pushContext.push(Constants.CHANNEL_CHANGING_MUSIC, musicDisplay);
					log.info("pushing to client ['"+musicDisplay.getMusicName()+"','"+musicDisplay.getRequester()+"']");
					
					/* force garbage collector */
//					System.gc();
					
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

}
