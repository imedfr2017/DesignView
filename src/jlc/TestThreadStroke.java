package jlc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

public class TestThreadStroke extends JFrame
//	implements ValueListener<Color>,HoverListener<Color>
{
	final private Border BLACK_BORDER = Config.BLACK_BORDER;
	final private Color PANEL_BACKGROUND = Config.PANEL_BACKGROUND;
	final private Insets GADGET_INSETS = Config.GADGET_INSETS;
	final private Dimension GADGET_SIZE = new Dimension(50,50);

	final private Color[] colorArray = {
		new Color(255,255,255),	// olive
		new Color(102,102,0),	// olive
		new Color(82,163,0),	// leaf green
		new Color(0,255,179),	// sea green
		new Color(0,102,102),	// slate blue
		new Color(102,0,102),		// purple
		new Color(0,255,255),		// turquoise
		new Color(204,51,0),		// umber
		new Color(255,255,0),		// yellow
	};
	private ArrayList<LinearGradientPaint> gPaints = new ArrayList<>();

	public TestThreadStroke() 
	{
		setTitle("Brightness");
		setLayout(new MigLayout("fill"));

		JPanel panel = new JPanel(new MigLayout("fill")){
			@Override protected void paintComponent(Graphics g) {
				RenderingHints hints = new RenderingHints(null);
				hints.put(
						RenderingHints.KEY_DITHERING,
						RenderingHints.VALUE_DITHER_ENABLE);
				hints.put(
						RenderingHints.KEY_STROKE_CONTROL,
						RenderingHints.VALUE_STROKE_NORMALIZE);
				Graphics2D g2 = (Graphics2D) g.create();
				Rectangle bounds = g2.getClipBounds();
				Line2D line = new Line2D.Float(
						0,0,
						bounds.width, bounds.height
								);
				g2.setStroke(new ThreadStroke(2,10));
				g2.draw(line);
//				drawGSLine(
//						g2,
//						10,
//						createBrightnessPaint(
//							new Color(255,0,0),
//							.5f,1,
//							24
//							),
//						0, bounds.height/2,
//						bounds.width, bounds.height/2
//						);
//				drawGSLine(
//						g2,
//						10,
//						createSaturationPaint(
//							new Color(255,0,0),
//							.1f,.5f,
//							24
//							),
//						0, bounds.height/2+20,
//						bounds.width, bounds.height/2+20
//						);
				g2.dispose();
			}
		};
		panel.setPreferredSize(new Dimension(400,100));
		add(panel,"grow");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocation(1400, 500);
		setVisible(true);
	}
	class ThreadStroke implements Stroke 
	{
	  final private BasicStroke stroke;
	  final private float period;

	  public ThreadStroke(float width, float period) {
		this.stroke = new BasicStroke(width);
		this.period = period;
	  }

	  public Shape createStrokedShape(Shape shape) {
		  Rectangle bounds = shape.getBounds();
		  System.out.println("shape bounds "+bounds);
		GeneralPath newshape = new GeneralPath(); // Start with an empty shape

		// Iterate through the specified shape, perturb its coordinates, and
		// use them to build up the new shape.
		float[] inXY = new float[6];
		float[] outXY = new float[6];
		float x1 = 0,y1 = 0,x2 = 0,y2 = 0;
		for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i
			  .next()) {
			int type = i.currentSegment(inXY);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				System.out.println("move to " + inXY[0] + " " + inXY[1]);
				x1 = inXY[0]; y1 = inXY[1];
				newshape.moveTo(x1,y1);
				break;
			case PathIterator.SEG_LINETO:
				System.out.println("line to " + inXY[0] + " " + inXY[1]);
				x2 = inXY[0]; y2 = inXY[1];
				float xl = x2-x1, yl = y2-y1;
				float len = (float) Math.sqrt(xl*xl+yl*yl);
				int np = (int)(len/period + .5f);
				float xd = xl/np, yd = yl/np;
				System.out.println(
						"   p1  " + x1 + "-" + y1
						+"\n   p2  " + inXY[0] + "-" + inXY[1]
//						+"\n   pc  " + xc + "-" + yc
						+"\n  len  " + len
						+"\n   np  " + np
						+"\n    d  " + xd + "-" + yd
						);
				float x = x1, y = y1, xc = 0, yc = 0;
				float xcd = (float) Math.sin(Math.PI) * xl*.25f,
						ycd = (float) Math.cos(Math.PI) *  yl * .25f;
				for(int n=0; n<np; n++) {
				System.out.println(
						"c1  " + x + "-" + y
						);
					newshape.moveTo(x,y);
					xc = x + xcd;
					yc = y + ycd;
					x += xd;
					y += yd;
					newshape.quadTo(xc,yc,x,y);
				}
//			newshape.curveTo(coords[0], coords[1], coords[2], coords[3],
//				coords[4], coords[5]);
				break;
			case PathIterator.SEG_CLOSE:
				System.out.println("close");
				newshape.closePath();
				break;
		  }
		}

		  System.out.println("done");
		// Finally, stroke the perturbed shape and return the result
		return stroke.createStrokedShape(newshape);
	  }
	}
	private void drawGSLine(
			Graphics2D g2,
			float thickness,
			LinearGradientPaint gp,
			float x1, float y1,
			float x2, float y2
			)
	{
		g2.setPaint(gp);
		Stroke s = new BasicStroke(thickness);
		Line2D line = new Line2D.Float(x1,y1,x2,y2);
		Shape sh = s.createStrokedShape(line);
		g2.fill(sh);
	}
	private LinearGradientPaint createBrightnessPaint(
			Color color,
			float bri1, float bri2,
			int nPixel
			)
	{
		float briLen = bri2 - bri1;
		float briInc = briLen/nPixel;
		float fracInc = 1f/nPixel;
		Color[] colors = new Color[nPixel];
		float[] fracs = new float[nPixel];

		float[] colorHSB = Color.RGBtoHSB(
				color.getRed(), color.getGreen(), color.getBlue(),
				null
				);

		float f = 0, h = colorHSB[0], s = colorHSB[1], b = bri1;
		for(int i=0; i<nPixel; i++) {
			fracs[i] = f;
			f += fracInc;
			int rgb = Color.HSBtoRGB(h,s,b);
			b += briInc;
			colors[i] = new Color(rgb);
		}

		return new LinearGradientPaint(
				0,0, nPixel,0,
				fracs, colors,
				MultipleGradientPaint.CycleMethod.REPEAT
				);
	}
	private LinearGradientPaint createSaturationPaint(
			Color color,
			float sat1, float sat2,
			int nPixel
			)
	{
		float satLen = sat2 - sat1;
		float satInc = satLen/nPixel;
		float fracInc = 1f/nPixel;
		Color[] colors = new Color[nPixel];
		float[] fracs = new float[nPixel];

		float[] colorHSB = Color.RGBtoHSB(
				color.getRed(), color.getGreen(), color.getBlue(),
				null
				);

		float f = 0, h = colorHSB[0], s = sat1, b = colorHSB[2];
		for(int i=0; i<nPixel; i++) {
			fracs[i] = f;
			f += fracInc;
			int rgb = Color.HSBtoRGB(h,s,b);
			s += satInc;
			colors[i] = new Color(rgb);
		}

		return new LinearGradientPaint(
				0,0, nPixel,0,
				fracs, colors,
				MultipleGradientPaint.CycleMethod.REPEAT
				);
	}
	public static void main(String args[]) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				new TestThreadStroke();
			}
		});
	}

}