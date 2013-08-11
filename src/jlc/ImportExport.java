package jlc;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ImportExport
{
	public static DesignPack fileToDesignPack(File file)
		throws Exception
	{
		DesignPack designPack = DesignParser.readFile(file);

		return designPack;
	}
	public static List<DesignPack> fileToDesignPack(List<File> fList)
	{
		ArrayList<DesignPack> dpList = new ArrayList<>();
		if ( fList == null || fList.isEmpty() ) return dpList;

		for(File file : fList) {
			try {
				DesignPack designPack = fileToDesignPack(file);
				dpList.add(designPack);
			} catch(Exception e) {
				String[] options = new String[]{
					"OK, Continue",
					"Stop importing"
				};

				int sel = JOptionPane.showOptionDialog(null,
					e.getMessage(),
					"Failed to import "+file.getName(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
					);
				if ( sel == 1 ) break;
			}
		}

		return dpList;
	}
	public static File designPackToFile(DesignPack designPack) 
		throws Exception
	{
		if ( designPack == null ) return null;

		File tempDir = Files.createTempDirectory(
				null).toFile();
		File tempFile = new File(tempDir,designPack.getFilename());
		FilePacks.write(designPack.getFilePack(),tempFile);

		return tempFile;
	}
	public static List<File> designPackToFile(List<DesignPack> dpList) 
	{
		ArrayList<File> fList = new ArrayList<>();
		File tempDir = null;

		if ( dpList == null || dpList.isEmpty() ) return fList;

		try {
			tempDir = Files.createTempDirectory(null).toFile();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null,
				e.getMessage(),
				"Failed to export designs (no temp dir)",
				JOptionPane.WARNING_MESSAGE
				);
			return fList;
		}

		FilePack fp = null;
		for(DesignPack dp : dpList) 
		{
			fp = dp.getFilePack();
			File tempFile = new File(tempDir,fp.getName());
			try {
				FilePacks.write(fp,tempFile);
				fList.add(tempFile);
			} catch(Exception e) {
				String[] options = new String[]{
					"OK, Continue",
					"Stop exporting"
				};

				int sel = JOptionPane.showOptionDialog(null,
					e.getMessage(),
					"Failed to export "+fp.getName(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
					);
				if ( sel == 1 ) break;
			}
		}
		return fList;
	}
}
