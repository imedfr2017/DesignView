package jlc;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public interface Design {
	String getName();
	String getExt();
	String getType();
	ArrayList<ThreadRun> getThreadRuns();
	Rectangle getBounds();
	int getWidth();
	int getHeight();
	void jumpTo(int x, int y);
	void setColor(Color color, String colorName);
	void stitchTo(int x, int y);
	void dumpThreadRuns();
}
