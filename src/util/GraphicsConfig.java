package util;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class GraphicsConfig {
	public static final GraphicsConfiguration DEFAULT_GC;
	public static final GraphicsDevice DEFAULT_GD;
	public static final int DEFAULT_DISPLAY_WIDTH;
	public static final int DEFAULT_DISPLAY_HEIGHT;
	public static final Graphics2D DEFAULT_G2D;
	public static final FontRenderContext DEFAULT_FRC;
	public static final Font DEFAULT_FONT;
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DEFAULT_GD = ge.getDefaultScreenDevice();
		DEFAULT_GC = DEFAULT_GD.getDefaultConfiguration();
		DEFAULT_DISPLAY_WIDTH = DEFAULT_GD.getDisplayMode().getWidth();
		DEFAULT_DISPLAY_HEIGHT = DEFAULT_GD.getDisplayMode().getHeight();

		BufferedImage bimage = DEFAULT_GC.createCompatibleImage(
				1,1, Transparency.OPAQUE);
		DEFAULT_G2D = (Graphics2D) bimage.getGraphics();

		DEFAULT_FONT = DEFAULT_G2D.getFont();
		DEFAULT_FRC = DEFAULT_G2D.getFontRenderContext();
	}
	public static GraphicsConfiguration getGraphicsConfiguration(
			Graphics2D g) {
		return ((Graphics2D) g).getDeviceConfiguration();
	}
	public static GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
	}
	public static final void smoothRenderingON(Graphics2D g2d)
	{
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
							RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
							RenderingHints.VALUE_STROKE_PURE);
	}
	public static final void smoothRenderingOFF(Graphics2D g2d)
	{
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
							RenderingHints.VALUE_RENDER_SPEED);
	}
	public static Point2D transformPoint(AffineTransform t, float xIn, float yIn)
	{
		Point2D p = new Point2D.Float(xIn,yIn);
		return t.transform(p,p);
	}
	public static String parseFileExtension(File file)
	{
		String ext = "";

		if ( file != null && !file.isDirectory() )
		{
			String[] parse = file.getName().split("\\.");
			if ( parse.length > 1 ) ext = parse[parse.length-1];
		}

		return ext;
	}
	public static RenderingHints qualityImageHints()
	{
		RenderingHints hints = new RenderingHints(null);
		hints.put(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC
				);
		hints.put(
				RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
				);
		return hints;
	}
	public static RenderingHints speedImageHints()
	{
		RenderingHints hints = new RenderingHints(null);
		hints.put(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF
				);
		hints.put(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR
				);
		hints.put(
				RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED
				);
		return hints;
	}
}
