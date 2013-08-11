package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerialIO
{
	public static SerialIn newInput(File file) throws Exception {
		return new SerialInImpl(file);
	}
	public static SerialOut newOutput(File file) throws Exception {
		return new SerialOutImpl(file);
	}
	private static class SerialInImpl implements SerialIn {
		ObjectInputStream in;

		public SerialInImpl(File file) throws Exception {
			in = new ObjectInputStream(new FileInputStream(file));
		}
		public Object read() throws Exception {
			return in.readObject();
		}
		public void close() throws Exception {
			in.close();
		}
	}
	private static class SerialOutImpl implements SerialOut {
		ObjectOutputStream out;

		public SerialOutImpl(File file) throws Exception {
			out = new ObjectOutputStream(new FileOutputStream(file));
		}
		public void write(Object object) throws Exception {
			out.writeObject(object);
		}
		public void close() throws Exception {
			out.close();
		}
	}
}
