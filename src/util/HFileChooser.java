package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HFileChooser extends JFileChooser
{
	private static File homeDir = new File(System.getProperty("user.home"));
	private JPopupMenu menu;
	private File historyFile;
	private String appName;
	private ArrayList<File> historyList = new ArrayList<File>();
	private int historyIndex;
	private PopListener popListener;

	private JButton backButton;
	private JButton forwardButton;

	public HFileChooser(String appName)
	{
		this(appName,null);
	}
	public HFileChooser(String appName, File startDirectory)
	{

		this.appName = appName;
		if ( startDirectory != null ) setCurrentDirectory(startDirectory);

// Snoop into the JFileChooser layout and add two extra buttons on the
// top row.   A backward button and forward button.

		JButton modelButton = null;
		JPanel upperRightPanel = null;
		try {
			BorderLayout layout = (BorderLayout) getLayout();
			if ( layout == null ) throw new Exception();

			JPanel northComp = (JPanel)
					layout.getLayoutComponent(BorderLayout.NORTH);
			if ( northComp == null ) throw new Exception();

			BorderLayout northLayout = (BorderLayout) northComp.getLayout();
			if ( northLayout == null ) throw new Exception();

			upperRightPanel = (JPanel)
					northLayout.getLayoutComponent(BorderLayout.LINE_END);
			if ( upperRightPanel == null ) throw new Exception();

			upperRightPanel.validate();

			if ( upperRightPanel.getComponentCount() < 1 )
				throw new Exception();

			modelButton = (JButton)upperRightPanel.getComponent(0);
		} catch(ClassCastException e) {
			return;
		} catch(Exception e) {
			return;
		}

		popListener = new PopListener();

		backButton = createIconButton("Back16.gif",modelButton);
		backButton.addMouseListener(popListener);
		backButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				if ( historyHasPrevious() )
				{
					File f = historyList.get(--historyIndex);
					setCurrentDirectory(f);
					setHistoryButtonsState();
				}
			}
		});

		forwardButton  = createIconButton("Forward16.gif",modelButton);
		forwardButton.addMouseListener(popListener);
		forwardButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				if ( historyHasNext() )
				{
					File f = historyList.get(++historyIndex);
					setCurrentDirectory(f);
					setHistoryButtonsState();
				}
			}
		});

		upperRightPanel.add(backButton);
		upperRightPanel.add(forwardButton);

	}

	@Override public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
		initHistory(appName);
		int r = super.showDialog(parent, approveButtonText);
		return r;
	}
	
	public void approveSelection()
	{
		super.approveSelection();

		File f = getSelectedFile();
		if ( !f.isDirectory() ) f = getCurrentDirectory();

		if ( ! inList(f) ) {
			historyList.add(f);
			historyIndex = historyList.size()-1;
		}
		saveHistory(appName);
	}
	private boolean inList(File file)
	{
		for(File f : historyList)
		{
			if ( f.getAbsolutePath().equals(file.getAbsolutePath()) )
				return true;
		}

		return false;
	}
	public static File queryHistoryCurrentDirectory(String appName) {
		HFileChooserHistory history = loadHistory(appName);
		if ( history.historyList.size() > 0 )
			return history.historyList.get(history.historyIndex);
		else return null;
	}
	private void initHistory(String appName)
	{
		HFileChooserHistory history = loadHistory(appName);
		historyList = history.historyList;
		historyIndex = history.historyIndex;

		if ( historyList.isEmpty() ) {
			setCurrentDirectory(homeDir);
			setHistoryButtonsState();
		} else {
			File cd = historyList.get(historyIndex);
			setCurrentDirectory(cd);
			if ( getFileSelectionMode() == JFileChooser.FILES_AND_DIRECTORIES
					|| getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY
				) {
				setSelectedFile(cd);
			}
			setHistoryButtonsState();
		}
	}
	private void saveHistory(String appName)
	{
		File historyFile = new File(homeDir+"\\"+appName+".ser");
		try {
			SerialOut out = SerialIO.newOutput(historyFile);
			HFileChooserHistory history = new HFileChooserHistory();
			history.historyList = historyList;
			history.historyIndex = historyIndex;
			out.write(history);
			out.close();
		} catch (Exception ex) {
			historyFile.delete();
			ex.printStackTrace();
		}
	}
	private static HFileChooserHistory loadHistory(String appName)
	{
		File historyFile = new File(homeDir+"\\"+appName+".ser");
		HFileChooserHistory history = new HFileChooserHistory();
		try {
			SerialIn in = SerialIO.newInput(historyFile);
			history = (HFileChooserHistory) in.read();
			in.close();
		} catch (Exception ex) {
			historyFile.delete();
		} 
		return history;
	}
	private boolean historyHasPrevious()
	{
		return historyIndex > 0 && !historyList.isEmpty();
	}
	private boolean historyHasNext()
	{
		return historyIndex < historyList.size()-1 && !historyList.isEmpty();
	}
	private void createHistoryPopup()
	{
		menu = new JPopupMenu();

		ActionListener listener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				historyIndex = Integer.parseInt(e.getActionCommand());
				setCurrentDirectory(historyList.get(historyIndex));
				setHistoryButtonsState();
			}
		};

		File cd = getCurrentDirectory();
		if ( historyList.contains(cd) ) {
			historyIndex = historyList.indexOf(cd);
		} else {
			historyList.add(cd);
			historyIndex = historyList.size()-1;
		}

		for( int index=0,end=historyList.size(); index<end; index++ )
		{
			boolean hilite = (index == historyIndex) ? true : false;

			menu.insert(
					newMenuItem(listener,historyList.get(index).getName(),
					historyList.get(index).getPath(),index+"",hilite),
					0
					);
		}
	}
	class PopListener extends MouseAdapter
	{
		public PopListener()
		{
		}
		@Override public void mousePressed(MouseEvent e)
		{
			popTrigger(e);
		}
		@Override public void mouseReleased(MouseEvent e)
		{
			popTrigger(e);
		}
		private void popTrigger(MouseEvent e)
		{
			if ( e.isPopupTrigger() && ! historyList.isEmpty() )
			{
				createHistoryPopup();
				Component c = (Component)e.getSource();
				int y = c.getY() + c.getHeight();
				menu.show((Component)e.getSource(),e.getX(),y);
			}
		}
	}
	private void setHistoryButtonsState()
	{
		if ( historyHasPrevious() ) backButton.setEnabled(true);
		else backButton.setEnabled(false);
		if ( historyHasNext() ) forwardButton.setEnabled(true);
		else forwardButton.setEnabled(false);
	}
	private JButton createIconButton(String imagePath, JButton model)
	{
		JButton button = null;

		ImageIcon iconImage = createIcon(imagePath);

		if ( iconImage == null ) button = new JButton("???");
		else {
			button = new JButton(iconImage);
			button.setPreferredSize(model.getPreferredSize());
			button.setMaximumSize(model.getMaximumSize());
			button.setBorder(model.getBorder());
		}

		return button;
	}
	private ImageIcon createIcon(String imagePath)
	{
		URL imgURL = this.getClass().getResource(imagePath);

		ImageIcon icon = new ImageIcon(imgURL);

		return icon;
	}
	private Component newMenuItem( ActionListener listener,
			String label, String toolTipText, String aCmd, boolean hilite)
	{
		String newLabel = label;

		JMenuItem m = new JMenuItem(newLabel);
		m.setActionCommand(aCmd);
		if ( toolTipText != null ) m.setToolTipText(toolTipText);
		m.addActionListener(listener);

		if ( hilite ) 
		{
			float[] colorB = m.getBackground().getRGBComponents(null);
			float[] colorF = m.getForeground().getRGBComponents(null);
			float[] colorH = new float[4];
			colorH[0] = (colorB[0]+colorF[0])/1.5f;
			colorH[1] = (colorB[1]+colorF[1])/1.5f;
			colorH[2] = (colorB[2]+colorF[2])/1.5f;
			colorH[3] = colorB[3];
			Color color = new Color( colorH[0],colorH[1],colorH[2],colorH[3] );
			m.setBackground(color);
		}

		return m;
	}
	public void setExtensionFilters(String[] filteredExtensions) 
	{
		if ( filteredExtensions == null ) 
			throw new NullPointerException("Invalid argument: filteredExtensions is NULL");

		if ( filteredExtensions != null && filteredExtensions.length > 0 ) 
		{
			setAcceptAllFileFilterUsed(false);

			for(String extension : filteredExtensions)
			{
				String description = sillyWillySays(extension);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						description,extension);
				addChoosableFileFilter(filter);
			}
		}
	}
//
// getTypeDescription() will only work if the file parameter represents a real
// existing file.  DOH!   So to work around that a dummy empty file is created
// before asking for the type description.
//
	private String sillyWillySays(String extension) 
	{
		File file = new File("emptyfile."+extension);
		boolean filePreExists = file.exists();
		if ( !filePreExists )
			try { new FileOutputStream(file).close(); } catch(IOException e) {}
		String description = getTypeDescription(file);
		if ( !filePreExists) file.delete();

		return description;
	}
	public File getFilteredSelection()
	{
		File file = getSelectedFile();
		if ( file != null )
		{
			FileNameExtensionFilter filter = (FileNameExtensionFilter) getFileFilter();
			if ( filter != null ) 
			{
				if ( !validExtension(parseExtension(file)) )
					file = new File(file.getPath()+"."+filter.getExtensions()[0]);
			}
		}
		return file;
	}
	private boolean validExtension(String extension) 
	{
		if ( extension == null ) 
			throw new NullPointerException("Invalid argument: extension is NULL");

		for(FileFilter filter : getChoosableFileFilters())
		{
			if ( filter instanceof FileNameExtensionFilter )
			{
				FileNameExtensionFilter eFilter = (FileNameExtensionFilter) filter;
				if ( eFilter.getExtensions()[0].equalsIgnoreCase(extension) )
					return true;
			}
		}
		return false;
	}
	public String parseDirectory(File file) {
		if ( file == null ) 
			throw new NullPointerException("Invalid argument: file is NULL");

		if ( file.isDirectory() ) return file.getPath();

		return file.getParent();
	}
	public String parseFilename(File file) {
		if ( file == null ) 
			throw new NullPointerException("Invalid argument: file is NULL");

		if ( file.isDirectory() ) return "";

		return file.getName();
	}
	public String parseExtension(File file) {
		if ( file == null ) 
			throw new NullPointerException("Invalid argument: file is NULL");

		if ( file.isDirectory() ) return "";

		if ( file.getName() == null ) return "";

		String[] parsed = file.getName().split("\\.");

		if ( parsed.length == 1 ) return "";

		return parsed[parsed.length-1];
	}
}