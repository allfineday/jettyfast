package com.jettyweb.conf;

import com.jettyweb.log.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertiesInfo implements FileHandler {

	protected String fileName;

	public PropertiesInfo(String fileName) {
		super();
		this.fileName = fileName;
		FileWatcher.inst.addHandle(this);
	}

	Properties pro = new Properties();

	public String get(String key) {
		return pro.getProperty(key);
	}

	@Override
	public URL[] listFile() {
		URL url = PropertiesInfo.class.getClassLoader().getResource(fileName);
		if (url == null) {
			Log.get(PropertiesInfo.class).info("{} cannot found", fileName);
			return null;
		}
		return new URL[] { url };
	}

	@Override
	public void deal(InputStream in) throws Exception {
		if (in == null) {
			return;
		}
		Properties temp = new Properties();
		temp.load(in);
		pro = temp;
	}

}
