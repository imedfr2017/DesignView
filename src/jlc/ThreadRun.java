package jlc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

public class ThreadRun {
	private ArrayList<Stitch> stitchList;
	private Color color;
	private String colorName;
	private int threadNum;

	public ThreadRun(
			final ArrayList<Stitch> stitchList,
			final Color color, 
			final String colorName,
			final Integer threadNum
			) 
	{
		this.stitchList = Objects.requireNonNull(stitchList);
		this.color = Objects.requireNonNull(color);
		this.colorName = Objects.requireNonNull(colorName);
		this.threadNum = Objects.requireNonNull(threadNum);
	}
	public ArrayList<Stitch> getStitchList() {
		return stitchList;
	}
	public int getNumStitches() {
		return stitchList.size();
	}
	public Color getColor() {
		return color;
	}
	public String getColorName() {
		return colorName;
	}
	public int getThreadNum() {
		return threadNum;
	}
	@Override public String toString() {
		return "Thread run ("+colorName+") ("+getNumStitches()+" stitches)";
	}
}
