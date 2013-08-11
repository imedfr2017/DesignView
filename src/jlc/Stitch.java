package jlc;

import java.awt.Color;
import java.awt.geom.Line2D;

public class Stitch {
	private int x1,y1,x2,y2;
	private Color color;

	public Stitch(int x1, int y1, int x2, int y2, Color color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
	}
	public Line2D getLine() {
		return new Line2D.Double(x1, y1, x2, y2);
	}
	public Color getColor() {
		return color;
	}
	@Override public String toString() {
		return "("+x1+","+y1+")-("+x2+","+y2+") ("+color+")";
	}
}
