package app.util;

import static com.qotsa.jni.controller.WinampController.*;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.qotsa.exception.InvalidHandle;

public class WinampUtils {
	private static final Logger log = LogUtils.getLogger(WinampUtils.class);
	
	private static final String ACTION_STOP = "stop";
	private static final String ACTION_PAUSE = "pause";
	private static final String ACTION_RESUME = "resume";
	private static final String ACTION_PLAY = "play";
	private static final String ACTION_RUN = "run";
	private static final String ACTION_NEXT = "next";
	private static final String ACTION_BACK = "back";
	
	public static final String SYNCHRONIZE_MODE_WINAMP = "1";
	public static final String SYNCHRONIZE_MODE_WEB = "2";

	/**
	 * do anything follow from command
	 * 
	 * @param event
	 *            of the action from form
	 * @return String of status of winamp
	 */
	public static String playerControl(String event) {
		log.debug("Enter playerControl : "+event);
		
		try {
			
			if (ACTION_RUN.equalsIgnoreCase(event)) {
				run();
			} else if (ACTION_PLAY.equalsIgnoreCase(event)) {
				play();
				return "playing";
			} else if (ACTION_RESUME.equalsIgnoreCase(event)) {
				resume();
				return "resumed";
			} else if (ACTION_PAUSE.equalsIgnoreCase(event)) {
				pause();
				return "pause";
			} else if (ACTION_STOP.equalsIgnoreCase(event)) {
				stop();
				return "stopped";
			} else if (ACTION_NEXT.equalsIgnoreCase(event)) {
				nextTrack();
			} else if (ACTION_BACK.equalsIgnoreCase(event)) {
				previousTrack();
			}

		} catch (InvalidHandle ihe) {
			log.error("Error in playerControl", ihe);
		} catch (Exception e) {
			log.error("Error in playerControl", e);
		} finally {
			log.debug("Quit playerControl");
		}
		return null;
	}
	
	/**
	 * append file to winamp playlist
	 * @param fileName filename without path 
	 */
	public static void appendFileToPlaylist(String fileName) {
		log.debug("Enter appendFileToPlaylist");
        try {
//			WinampController.appendToPlayList(FileUtils.ABSOLUTEPATH_THE_GREAN_GANG_RADIO+name);
        	
        	//using command-line to avoid UTF-8 problem of WinampController.appendToPlayList 
        	StringBuilder command = new StringBuilder(); 
        	command.append("cmd /C");
        	command.append("\"");
        	command.append("\"C:/Program Files (x86)/winamp/winamp.exe\"");
        	command.append(" /add ");
        	command.append("\"" + FileUtils.ABSOLUTEPATH_THE_GREAN_GANG_RADIO + fileName + "\"");
        	command.append("\"");
        	
        	log.debug(command.toString());
        	Process p = Runtime.getRuntime().exec(command.toString());
//        	p.waitFor();
		} catch (IOException e) {
			log.error("Error in onTransfer",e);
		} finally {
			log.debug("Quit appendFileToPlaylist");
		}
	}
	/**
	 * get playlist from webpage and synchronize to winamp's playlist
	 */
	public static void synchronizePlaylist(String synchronizeMode) {

	}

	/**
	 * check web's playlist length & winamp's playlist length for the way to
	 * sync.
	 * 
	 * @return {@link #SYNCHRONIZE_MODE_WINAMP} or {@link #SYNCHRONIZE_MODE_WEB}
	 * @throws InvalidHandle
	 */
	public static String synchronizeChecking() throws InvalidHandle {
		int winampPlaylistLength = getPlayListLength();
		int webPlaylistLength = 0; // TODO

		return winampPlaylistLength > webPlaylistLength ? SYNCHRONIZE_MODE_WINAMP : SYNCHRONIZE_MODE_WEB;

	}
		

}
