package jlc;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.String;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

//	TODO 0  finish cut n paste, Swing handler, keys
//	TODO 0  finish cut n paste, AWT handler, keys
//	TODO 0  finish drag n drop, Swing handler
//	TODO 0  finish drag n drop, AWT handler
//	TODO 0  store files internally, rather than a path reference
//	TODO 1  show thread color names as found in pes file
//	TODO 1  try to map PES thread color names to a color table (from net)
//	TODO 1  add optional single folder arg, dnd and ccp support
//

public class DesignView 
{
	private final String APP_NAME;
	private final Color PANEL_BACKGROUND = Config.PANEL_BACKGROUND;
	private final static String[] SUPPORTED_EXTS = {"pes","hus"};
	private final SelectedDesignPanel selectedDesignPanel;
	private Dimension frameSize;

	public DesignView(String[] args) throws Exception
	{
		APP_NAME = getClass().getSimpleName();

//		installWindowsRegKeys(
//			"PesView",
//			".pes",
//			"F:\\NetBeansProjects\\PesView\\dist",
//			"PesView.jar",
//			"PesView.ico"
//			);

		calcLayout();
		selectedDesignPanel = new SelectedDesignPanel(
				);

		File argFile = extractFileFromArgs(args);
		if ( argFile != null ) selectedDesignPanel.setDesignPack(
				ImportExport.fileToDesignPack(argFile)
				);

		new InstallDragNDrop(
				selectedDesignPanel
				);

		new InstallCopyPasteRemoveMenu(
				selectedDesignPanel
				);

		JPanel outer_PNL = new JPanel(new MigLayout(
				"fill"
				));

		outer_PNL.setBackground(PANEL_BACKGROUND);
		outer_PNL.add(selectedDesignPanel,"grow");

		final JFrame frame = new JFrame();
		frame.addWindowListener( new WindowAdapter() {
				@Override public void windowClosing(WindowEvent e) {
					frame.dispose();
				}
			}
		);
		frame.setLayout(new MigLayout("fill"));
		frame.setPreferredSize(frameSize);
		frame.getContentPane().setBackground(PANEL_BACKGROUND);


		frame.add(outer_PNL,"grow");

		frame.pack();
		frame.setVisible(true);

		// Set pref size to whatever the window is resized to.
		// This prevents the layout manager from resizing the frame
		// automatically when any change causes a re-layout.  Such
		// as dropping designs in the window.
		frame.addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {
				Dimension size = e.getComponent().getSize();
				frame.setPreferredSize(size);
			}
		});
	}
	private File extractFileFromArgs(String[] args) 
	{
		Objects.requireNonNull(args);

		if ( args.length == 0 ) return null;

		String p = "";
		for(String a : args) p += (a+" ");

		return new File(p);
	}
	private void calcLayout() {
		Rectangle maxBounds = maxDesktopBounds();

		int outerPanelHeight = (int)(.50 * maxBounds.height);

		int outerPanelWidth = outerPanelHeight;

		frameSize = new Dimension(
				outerPanelWidth, outerPanelHeight);
	}
	public Rectangle maxDesktopBounds()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();
	}
	// Args may be one or more design files.
	//
    public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				try {
					new DesignView(args);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
	private static void installWindowsRegKeys(
			String appName,
			String ext,
			String pgmPath,
			String pgmJar,
			String iconFilename
			) 
	{
		Objects.requireNonNull(appName);
		Objects.requireNonNull(ext);
		Objects.requireNonNull(pgmPath);
		Objects.requireNonNull(pgmJar);
		Objects.requireNonNull(iconFilename);

//		installRegFileExt(appName, ext);
		installAppKeys(
				appName, 
				"javaw -jar "+pgmPath+"\\"+pgmJar+" %1%"
				);
		installAppAssocKeys(
				appName,
				ext,
				pgmPath+"\\"+iconFilename
				);
	}
	private static void setExString(String key, String name, String value) {
		Advapi32Util.registrySetExpandableStringValue(
				WinReg.HKEY_CLASSES_ROOT,
				key,
				name,		// null represents "Default value"
				value
				);
	}
	private static boolean createKey(String key) {
		return Advapi32Util.registryCreateKey(
					WinReg.HKEY_CLASSES_ROOT,
					key
					);
	}
	private static void installAppKeys(
			String appName, String cmd)
	{
		createKey(appName+"\\Shell\\Open\\Command");
		setExString(appName+"\\Shell\\Open\\Command",null,cmd);
	}
	private static void installAppAssocKeys(
			String appName, String ext, String iconFilename)
	{
		createKey(ext+"\\DefaultIcon");
		setExString(ext,null,appName);
		setExString(ext+"\\DefaultIcon",null,iconFilename);
	}
	private void setWindowsAssoc() {
// cmd lines to associate a file type with and executable
//
// assoc .pes=Pesfile
// ftype Pesfile=javaw -jar F:\NetBeansProjects\PesView\dist\PesView.jar
//
	}
}
