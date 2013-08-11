package jlc;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

public class InstallCopyPasteRemoveMenu {

	public InstallCopyPasteRemoveMenu (
			final SelectedDesignPanel selectedDesignPanel
			)
	{
		Objects.requireNonNull(selectedDesignPanel);

		Action copyAction = new AbstractAction("Copy")
		{

			@Override public void actionPerformed(ActionEvent e) {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				if ( clip == null ) return;

				List<DesignPack> filelist = new ArrayList<>();
				filelist.add(selectedDesignPanel.getDesignPack());
				FileTransferable tf = new FileTransferable(
						ImportExport.designPackToFile(filelist));

				clip.setContents(tf, null);
			}
		};
		Action pasteAction = new AbstractAction("Paste")
		{
			@Override public void actionPerformed(ActionEvent evt) {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				if ( clip == null ) return;

				Transferable tf = clip.getContents(null);

				if ( !tf.isDataFlavorSupported(
						DataFlavor.javaFileListFlavor) ) return;
				try {
					List<File> fl = (List<File>) tf.getTransferData(
							DataFlavor.javaFileListFlavor);
					if ( fl == null || fl.isEmpty() ) return;

					List<DesignPack> dl = ImportExport.fileToDesignPack(fl);
					selectedDesignPanel.setDesignPack(dl.get(0));
				} catch(Exception e) {}
			}
		};
		installPopupMenu(
				selectedDesignPanel,
				copyAction,
				pasteAction
				);

	}
	public void installPopupMenu(
			final JComponent component,
			final Action copyAction,
			final Action pasteAction
			)
	{

		JPopupMenu menu = component.getComponentPopupMenu();
		if ( menu == null ) menu = new JPopupMenu();

		menu.add(copyAction);
		menu.add(pasteAction);

		String copyName = (String) copyAction.getValue(Action.NAME);
		String pasteName = (String) pasteAction.getValue(Action.NAME);

		ActionMap map = component.getActionMap();

		map.put(copyName,copyAction);
		map.put(pasteName,pasteAction);

		component.setActionMap(map);

		InputMap imap = component.getInputMap();

		imap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C,Event.CTRL_MASK),
				copyName
				);
		imap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_V,Event.CTRL_MASK),
				pasteName
				);

		component.setInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				imap
				);

		component.setComponentPopupMenu(menu);
	}
}
