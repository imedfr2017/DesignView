package jlc;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public class PesDesign implements Design {
	private String name,ext,type;
	private ArrayList<Stitch> stitchList = new ArrayList<>();
	private ArrayList<ThreadRun> threadRuns = new ArrayList<>();
	private ThreadRun currentRun;
	private int xPos, yPos, xMin, xMax, yMin, yMax;
	private Color color;
	private int width,height, threadNum;

	public PesDesign() {
	}

	public PesDesign(String name, String ext, String type,
			int width, int height) {
		this.name = name;
		this.ext = ext;
		this.type = type;
		this.width = width;
		this.height = height;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getExt() {
		return ext;
	}
	@Override
	public String getType() {
		return type;
	}
	@Override
	public int getWidth() {
		return width;
	}
	@Override
	public int getHeight() {
		return height;
	}
	@Override
	public void stitchTo(int x, int y) {
		if ( color == null ) throw new RuntimeException(
				"Color not set for first stitch");
		int xPos2 = xPos + x;
		int yPos2 = yPos + y;
		Stitch stitch = new Stitch(xPos, yPos, xPos2, yPos2,color);
		xPos = xPos2;
		yPos = yPos2;
		stitchList.add(stitch);
		minMax(xPos,yPos);
//			System.out.println("("+x+","+y+") "+stitch);
	}
	@Override
	public void jumpTo(int x, int y) {
		if ( color == null ) throw new RuntimeException(
				"Color not set for first stitch");
		xPos += x;
		yPos += y;
		minMax(xPos,yPos);
//			System.out.println("jump "+xPos+","+yPos);
	}
	private void minMax(int x, int y) {
		xMin = Math.min(x,xMin);
		xMax = Math.max(x,xMax);
		yMin = Math.min(y,yMin);
		yMax = Math.max(y,yMax);
	}
	@Override
	public void setColor(Color color, String colorName) {
		this.color = color;
		stitchList = new ArrayList<>();
		currentRun = new ThreadRun(stitchList,color,colorName,threadNum++);
		threadRuns.add(currentRun);
	}
	@Override
	public Rectangle getBounds() {
		return new Rectangle(xMin,yMin,xMax-xMin+1,yMax-yMin+1);
	}

	@Override
	public ArrayList<ThreadRun> getThreadRuns() {
		return threadRuns;
	}
	@Override public void dumpThreadRuns() {
		System.out.println("Thread runs for "+name);
		for(ThreadRun tr : threadRuns) {
			System.out.println(tr);
		}
	}
}
