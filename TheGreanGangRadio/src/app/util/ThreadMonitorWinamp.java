package app.util;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;


public class ThreadMonitorWinamp extends Thread {
	private String playingMusic;

	@Override
	public void run() {
		while (true) {
			try {
				playingMusic = WinampUtils.getFileNamePlaying();
				PushContext pushContext = PushContextFactory.getDefault().getPushContext();
				pushContext.push("/notifications", playingMusic);
				
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
    public String getPlayingMusic() {
        return playingMusic;
    }
}
