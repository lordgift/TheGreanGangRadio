package app.util;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;

import org.apache.log4j.Logger;

public class FileUtils {	
	private static final Logger log = LogUtils.getLogger(FileUtils.class);
	
	private static final FileUtils INSTANCE = new FileUtils();
	private static boolean isInitial = false;
	public static final String ABSOLUTEPATH_THE_GREAN_GANG_RADIO = "D:/TheGreanGangRadio/";

	
	private FileUtils(){};
	public static FileUtils getInstance() {
		//isInitial is boolean to only do this method only one time
		if(!isInitial) INSTANCE.initialEnvironment();
		return INSTANCE == null ? new FileUtils() : INSTANCE;
	}
	
	/**
	 * make directory to be main directory of program
	 */
	public void initialEnvironment() {
		log.debug("Enter initialEnvironment");
		
		//root of TheGreanGangRadio Environment
		File theGreanGangFolder = new File(ABSOLUTEPATH_THE_GREAN_GANG_RADIO);
		if(!theGreanGangFolder.exists()) {
			theGreanGangFolder.mkdir();
			log.debug("path to place music has been created at " + ABSOLUTEPATH_THE_GREAN_GANG_RADIO);
		}
		
		isInitial = true;
		log.debug("Quit initialEnvironment");
	}	
	
	/**
	 * use for get all music name in specific folder 
	 * 
	 * @return list of music name
	 */
	public List<String> getMusicListFromDirectory() {
		log.debug("Enter getMusicListFromDirectory");
		
		List<String> musicNames = new ArrayList<String>();
		
		File directoryFile = new File(ABSOLUTEPATH_THE_GREAN_GANG_RADIO);		
		for (File file : directoryFile.listFiles()) {
			musicNames.add(file.getName());
		}
//		log.debug(musicNames);		
		
		log.debug("Quit getMusicListFromDirectory");
		return musicNames;
	}
	
	/**
	 * copy file from {@link InputStream} , which uploading file to destination output
	 * <BR /> and reduce two of more spaces to only one for new name
	 * @param fileName 
	 * @param in
	 */
	public void copyFile(String fileName, InputStream in) {
		log.debug("Enter copyFile");
		try {

			//for avoid problem with two or more spaces, this will replace to single
			String newFileName = fileName.replaceAll("( )+", " ");
			
			// write the inputStream to a FileOutputStream
			OutputStream out = new FileOutputStream(new File(ABSOLUTEPATH_THE_GREAN_GANG_RADIO + newFileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();

			log.debug("created : " + fileName);
		} catch (IOException e) {			
			log.error("Error in copyFile", e);
		} finally {
			log.debug("Quit copyFile");
		}
	}
}
