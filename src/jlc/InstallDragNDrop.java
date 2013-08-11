package jlc;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;

public class InstallDragNDrop 
{
	private final SelectedDesignPanel selectedDesignPanel;

	public InstallDragNDrop (
			final SelectedDesignPanel selectedDesignPanel
			)
	{
		this.selectedDesignPanel =
				Objects.requireNonNull(selectedDesignPanel);

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(
				selectedDesignPanel,
				DnDConstants.ACTION_COPY,
				new SelectedDesignDragListener()
				);

		new FileListDropTarget(selectedDesignPanel);
	}
//
// DragGestureListener class
//
	private class SelectedDesignDragListener  implements DragGestureListener
	{
		@Override
		public void dragGestureRecognized(DragGestureEvent event)
		{
			Cursor cursor = null;

			if (event.getDragAction() == DnDConstants.ACTION_COPY) {
				cursor = DragSource.DefaultCopyDrop;
			}

			DesignPack designPack = selectedDesignPanel.getDesignPack();

			try {
				File file = ImportExport.designPackToFile(designPack);
				FileTransferable tf = new FileTransferable(file);
				event.startDrag(cursor, tf);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null,
					e.getMessage(),
					"Failed to export "+designPack.getFilename(),
					JOptionPane.WARNING_MESSAGE
					);
			}
		}
	}
//
// FileList DropTarget class
//
	private class FileListDropTarget extends DropTargetAdapter
		implements DropTargetListener
	{
		private SelectedDesignPanel selectedDesignPanel;

		public FileListDropTarget(
				final SelectedDesignPanel selectedDesignPanel
				)
		{
			this.selectedDesignPanel = selectedDesignPanel;
			new DropTarget(
					selectedDesignPanel,
					DnDConstants.ACTION_COPY,
					this,
					true,
					null
					);
		}
		@Override public void drop(DropTargetDropEvent event)
		{
			try {
				if ( event.isDataFlavorSupported(
						DataFlavor.javaFileListFlavor) ) 
				{
					event.acceptDrop(DnDConstants.ACTION_COPY);
					Transferable tr = event.getTransferable();
					List<File> fl = (List<File>) tr.getTransferData(
							DataFlavor.javaFileListFlavor);
					List<DesignPack> dl = 
							ImportExport.fileToDesignPack(fl);
					selectedDesignPanel.setDesignPack(dl.get(0));
				}
			} catch(Exception e) {
				e.printStackTrace();
				event.rejectDrop();
			}
		}
	}
}
