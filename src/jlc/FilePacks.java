package jlc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.zip.CRC32;

public class FilePacks {

	public static FilePack create() 
	{
		return new FilePackImpl();
	}
	public static FilePack create(File file) 
		throws IOException
	{
		Objects.requireNonNull(file);

		DataInputStream inStream = 
				new DataInputStream(new FileInputStream(file));

		int sizeFile = (int) file.length();
		byte[] bytes = new byte[sizeFile];

		inStream.read(bytes);

		inStream.close();

		ByteBuffer buffer = ByteBuffer.wrap(bytes,0,sizeFile);
		CRC32 crc32 = new CRC32();
		long crc = 0;
		crc32.update(bytes);
		crc = crc32.getValue();

		FilePack filePack = new FilePackImpl(file.getName(),buffer,crc);

		return filePack;
	}
	public static void write(FilePack filePack, File file) 
		throws IOException
	{
		Objects.requireNonNull(filePack);
		Objects.requireNonNull(file);

		DataOutputStream outStream = 
				new DataOutputStream(new FileOutputStream(file));

		outStream.write(filePack.getFileBytes());
		
		outStream.close();
	}
	private static class FilePackImpl implements FilePack
	{
		private String name,ext;
		private ByteBuffer buffer;
		private long crc;

		public FilePackImpl() {
		}
		private FilePackImpl(String name, ByteBuffer buffer, long crc) 
		{
			setName(name);
			this.buffer = buffer;
			this.crc = crc;
		}
		@Override public String getName() {
			return name;
		}
		@Override public void setName(String name) 
		{
			this.name = name;

			int periodI = name.lastIndexOf(".");
			if ( periodI > 0 && name.length() > periodI ) 
					ext = name.substring(periodI+1);
			else ext = "";

			ext = ext.toLowerCase();
		}
		@Override public String getExt() {
			return ext;
		}
		@Override public ByteBuffer getFileBuffer() {
			return buffer;
		}
		@Override public byte[] getFileBytes() {
			return buffer.array();
		}
		@Override public int getFileLength() {
			return buffer.array().length;
		}

		@Override public int hashCode() {
			int hash = 5;
			hash = 37 * hash + (int) (this.crc ^ (this.crc >>> 32));
			return hash;
		}

		@Override public boolean equals(Object obj) 
		{
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			final FilePackImpl other = (FilePackImpl) obj;

			if (this.crc != other.crc) return false;
			
			if ( !this.getName().equalsIgnoreCase(other.getName()) ) 
				return false;
			
			return true;
		}
	}
}
