package com.jettyweb.conf;


import com.jettyweb.exception.SystemException;
import com.jettyweb.log.Log;
import com.jettyweb.util.Assert;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * 需要[]来分段<BR>
 * 以#注释
 * 
 * @author youtl
 *
 */
public final class ResUtils {

	public static DBResource dbResource(String db) throws Exception {
		String resourceFactory = AppInfo.get("sumk.db.resource.factory." + db, "DBFileFactory");
		resourceFactory = "org.yx.conf." + resourceFactory;
		Class<?> factoryClz = Class.forName(resourceFactory);
		Assert.isTrue(DBResourceFactory.class.isAssignableFrom(factoryClz),
				resourceFactory + " should extend from DBResourceFactory");
		DBResourceFactory factory = (DBResourceFactory) factoryClz.newInstance();
		return factory.create(db);
	}

	public static HashMap<String, Properties> parseIni(String filename) throws FileNotFoundException {
		return parseIni(new FileInputStream(filename));
	}

	public static HashMap<String, Properties> parseIni(InputStream in) {
		return new IniFile(in).sections;
	}

	private static class IniFile {
		protected HashMap<String, Properties> sections = new HashMap<>();
		private String currentSecion;
		private Properties current;

		IniFile(InputStream in) {
			if (in == null) {
				SystemException.throwException(245323425, "ini stream cannot be null");
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				read(reader);
			} catch (Exception e) {
				Log.printStack(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception e) {
					}
				}

			}
		}

		protected void read(BufferedReader reader) throws IOException {
			String line;
			while ((line = reader.readLine()) != null) {
				parseLine(line);
			}
		}

		protected void parseLine(String line) {
			line = line.trim();
			if (line.matches("^\\[.*\\]")) {
				currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
				current = new Properties();
				sections.put(currentSecion, current);
			} else if (line.matches(".*=.*")) {
				if (current != null && !line.startsWith("#")) {
					int i = line.indexOf('=');
					String name = line.substring(0, i).trim();
					String value = line.substring(i + 1).trim();
					if (value.isEmpty()) {
						return;
					}
					current.setProperty(name, value);
				}
			}
		}

		@Override
		public String toString() {
			return this.sections.toString();
		}
	}

}
