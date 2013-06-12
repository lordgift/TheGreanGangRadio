package app.util;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.log4j.Logger;

public class FileUtils {
	private static final Logger log = LogUtils.getLogger(FileUtils.class);
	
	public static void main(String[] args) {
		getMusicListFromDirectory("D:\\Song\\Bodyslam\\(2010) Live In คราม\\Disc 1");
	}
	
	/**
	 * use for get all music name in specific folder 
	 * 
	 * @param musicDirectory path of specific folder 
	 * @return list of music name
	 */
	public static List<String> getMusicListFromDirectory(String musicDirectory) {
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
	
	public static void uploadFile() {
		//TODO
	}
}
