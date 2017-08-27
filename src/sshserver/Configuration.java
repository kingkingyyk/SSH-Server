package sshserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Configuration {

	private static final File SettingsFile=new File(SSHServer.WorkingDir.getPath()+File.separator+"config.conf");
	private static final Properties SETTINGS=new Properties();
	private static final String SETTINGS_KEY_USERNAME="username";
	private static final String SETTINGS_KEY_PASSWORD="password";
	private static final String SETTINGS_KEY_PORT="port";
	
	public static String getUsername() {
		return SETTINGS.getProperty(SETTINGS_KEY_USERNAME);
	}
	
	public static String getPassword() {
		return SETTINGS.getProperty(SETTINGS_KEY_PASSWORD);
	}
	
	public static int getPort() {
		return Integer.parseInt(SETTINGS.getProperty(SETTINGS_KEY_PORT));
	}
	
	public static void setUsername(String s) {
		SETTINGS.setProperty(SETTINGS_KEY_USERNAME,s);
	}
	
	public static void setPassword(String p) {
		SETTINGS.setProperty(SETTINGS_KEY_PASSWORD,p);
	}
	
	public static void setPort(int p) {
		SETTINGS.setProperty(SETTINGS_KEY_PORT,String.valueOf(p));
	}
	
	public static void loadSettings() {
		SETTINGS.setProperty(SETTINGS_KEY_USERNAME, "username");
		SETTINGS.setProperty(SETTINGS_KEY_PASSWORD, "password");
		SETTINGS.setProperty(SETTINGS_KEY_PORT, "22");
		
		if (SettingsFile.exists()) {
			try {
				FileInputStream fis=new FileInputStream(SettingsFile);
				SETTINGS.load(fis);
				fis.close();
			} catch (Exception e) {}
		} else {
			ConfigurationUI ui=new ConfigurationUI(true);
			ui.setLocationRelativeTo(null);
			ui.setVisible(true);
		}
	}

	public static void writeSettings () throws Exception {
		SettingsFile.getParentFile().mkdirs();
		FileOutputStream fos=new FileOutputStream(SettingsFile);
		SETTINGS.store(fos,"");
		fos.close();
	}
}
