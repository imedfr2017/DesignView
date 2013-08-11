package jlc;

import java.nio.ByteBuffer;

public interface FilePack {
	ByteBuffer getFileBuffer();
	byte[] getFileBytes();
	String getName();
	void setName(String name);
	String getExt();
	int getFileLength();
	@Override int hashCode();
	@Override boolean equals(Object obj);
}