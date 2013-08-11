package util;

import java.io.File;

public class App 
{
	final private static String userHome = System.getProperty("user.home");
	final private String name, version, settingsPath, outputPath;
	final private File settingsFolder, outputFolder;

	public App(String name, String version) {
		this.name = name;
		this.version = version;

		settingsPath = userHome+"\\"+name;
		settingsFolder = new File(settingsPath);
		if ( !settingsFolder.exists() ) settingsFolder.mkdir();

		outputPath = userHome+"\\"+name+"\\output";
		outputFolder = new File(outputPath);
		if ( !outputFolder.exists() ) outputFolder.mkdir();
		
	}
	public static String getUserHome() {
		return userHome;
	}
	public String getName() {
		return name;
	}
	public String getVersion() {
		return version;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public File getSettingsFolder() {
		return settingsFolder;
	}
	public File getOutputFolder() {
		return outputFolder;
	}
}
