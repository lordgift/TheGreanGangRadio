package app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtils {

	static {
		try {
			Properties log4jProperties = new Properties();

			// if use this must export log4j.properties as .jar then fill path in .jar into variable
			String logFile = "log4j/log4j.properties";

			// if use this must place log4j.properties in WEB-INF/classes/
			// String logFile = "log4j.properties";

			InputStream logStream = LogUtils.class.getClassLoader().getResourceAsStream(logFile);
			log4jProperties.load(logStream);
			
			PropertyConfigurator.configure(log4jProperties);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Logger getLogger(Class<?> clazz) {
		return Logger.getLogger(clazz);
	}
}
