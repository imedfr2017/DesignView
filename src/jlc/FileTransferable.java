package jlc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileTransferable  implements Transferable
{
	private FileListSource fileListSource;

	public FileTransferable (final File... files) 
	{
		fileListSource = new FileListSource() {
			@Override public List<File> getFileList() {
				if ( files == null ) 
					return Collections.EMPTY_LIST;
				else 
					return new ArrayList<File>(Arrays.asList(files));
			}
		};
	}
	public FileTransferable (final List<File> fileList)
	{
		fileListSource = new FileListSource() {
			@Override public List<File> getFileList() {
				if ( fileList == null ) 
					return Collections.EMPTY_LIST;
				else 
					return new ArrayList<File>(fileList);
			}
		};
	}
	public FileTransferable (FileListSource fileListSource) 
	{
		Objects.requireNonNull(fileListSource);
		this.fileListSource = fileListSource;
	}

	@Override public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.javaFileListFlavor};
	}

	@Override public boolean isDataFlavorSupported(
			DataFlavor flavor) 
	{
		if ( flavor.equals(DataFlavor.javaFileListFlavor) )
			return true;

		return false;
	}
	@Override public Object getTransferData(
			DataFlavor flavor) 
		throws UnsupportedFlavorException, IOException 
	{
		return fileListSource.getFileList();
	}
}