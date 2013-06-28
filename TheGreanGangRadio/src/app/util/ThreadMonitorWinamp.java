package app.util;

import org.apache.log4j.Logger;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;


public class ThreadMonitorWinamp extends Thread {
	private Logger log = LogUtils.getLogger(ThreadMonitorWinamp.class);
	
	private String playingMusic;
	private String playedMusic;

	@Override
	public void run() {
		while (true) {
			try {
				playingMusic = WinampUtils.getFileNamePlaying();

				if (!playingMusic.equals(playedMusic)) {
					PushContext pushContext = PushContextFactory.getDefault().getPushContext();
					pushContext.push(Constants.CHANNEL_CHANGING_MUSIC, playingMusic);
					log.debug("pushing to client");
				}
				playedMusic = playingMusic;
				
				sleep(3000);
			} catch (InterruptedException e) {
				log.error("Error in run",e);
				break;
			}
		}
	}
	
    public String getPlayingMusic() {
        return playingMusic;
    }
}
