package com.allpago.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


public class PropUtil {
	private static String PROPERTIES_FILE = "config.properties";
	private static Properties properties = new Properties();
	private static volatile PropUtil instance = new PropUtil();

	private PropUtil(){
		InputStream is=null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			//is = PropUtil.class.getResourceAsStream(PROPERTIES_FILE);
			properties.load(is);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Unable to load properties file:"+PROPERTIES_FILE);
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was some error while loading properties file:"+PROPERTIES_FILE);
			System.exit(-1);
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("There was some error while closing inputstream of the properties file:"+PROPERTIES_FILE);
				}
		}
	}

	public static PropUtil getInstance() {
		if(instance==null){
			instance = new PropUtil();
		}
		return instance;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
