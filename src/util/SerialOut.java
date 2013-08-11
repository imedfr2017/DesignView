package util;

public interface SerialOut {
	void write(Object object) throws Exception;
	void close() throws Exception;
}
