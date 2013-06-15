package app.util;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;

import org.apache.log4j.Logger;

public class FileUtils {
	private static final FileUtils INSTANCE = new FileUtils();
	private static final Logger log = LogUtils.getLogger(FileUtils.class);
	
	public static final String ABSOLUTEPATH_THE_GREAN_GANG_RADIO = "D:/TheGreanGangRadio/";

	
	private FileUtils(){};
	public static FileUtils getInstance() {
		INSTANCE.initialEnvironment();
		return INSTANCE == null ? new FileUtils() : INSTANCE;
	}
	
	public void initialEnvironment() {
		log.debug("Enter initialEnvironment");
		
		//root of TheGreanGangRadio Environment
		File theGreanGangFolder = new File(ABSOLUTEPATH_THE_GREAN_GANG_RADIO);
		if(!theGreanGangFolder.exists()) {
			theGreanGangFolder.mkdir();
			log.debug("path to place music has been created at " + ABSOLUTEPATH_THE_GREAN_GANG_RADIO);
		}
		
		log.debug("Quit initialEnvironment");
	}	
	
	/**
	 * use for get all music name in specific folder 
	 * 
	 * @param musicDirectory path of specific folder 
	 * @return list of music name
	 */
	public List<String> getMusicListFromDirectory(String musicDirectory) {
		log.debug("Enter getMusicListFromDirectory");
		
		List<String> musicNames = new ArrayList<String>();
		
		File directoryFile = new File(musicDirectory);		
		for (File file : directoryFile.listFiles()) {
			musicNames.add(file.getName());
		}
		log.debug(musicNames);
		
		log.debug("Quit getMusicListFromDirectory");
		return musicNames;
	}
	
	
	public void uploadFile() {
		//TODO
	}
}
