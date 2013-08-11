package util;

public interface SerialIn {
	Object read() throws Exception;
	void close() throws Exception;
}
