package util;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;

public class ImageUtils 
{
	public static BufferedImage createImage(BufferedImage image)
	{
		return new BufferedImage(image.getWidth(),image.getHeight(),
				image.getType());
	}
	public static BufferedImage createImage(Graphics2D g, double width, double height,
			int transparency) {
		return GraphicsConfig.getGraphicsConfiguration(g)
				.createCompatibleImage((int) width,(int) height,transparency);
	}
	public static BufferedImage createImage(double width, double height, int transparency) {
		return GraphicsConfig.getGraphicsConfiguration()
				.createCompatibleImage((int) width,(int) height,transparency);
	}
	public static BufferedImage createTransparentImage(double width, double height)
	{
		return createImage(width,height,Transparency.TRANSLUCENT);
	}
	public static BufferedImage createOpaqueImage(double width, double height)
	{
		return createImage(width,height,Transparency.OPAQUE);
	}
	public static BufferedImage createTransparentImage(Graphics2D g,double width, double height)
	{
		return createImage(g,width,height,Transparency.TRANSLUCENT);
	}
	public static BufferedImage createOpaqueImage(Graphics2D g,double width, double height)
	{
		return createImage(g,width,height,Transparency.OPAQUE);
	}
	public static BufferedImage createIndexedColorImage(double width, double height, Color[] colors)
	{
		int ncolors = colors.length;

		byte[] red = new byte[ncolors], 
				green = new byte[ncolors], 
				blue = new byte[ncolors];
		
		for(int i=0;i<ncolors;i++)
		{
			red[i] = (byte) colors[i].getRed();
			green[i] = (byte) colors[i].getGreen();
			blue[i] = (byte) colors[i].getBlue();
		}
		int nbits = 0, t = ncolors;
		while( t != 0) {
			nbits++;
			t = t >>> 1;
		}

		IndexColorModel icm = new IndexColorModel(nbits, ncolors, red, green, blue);
		BufferedImage image = 
				new BufferedImage((int) width,(int) height,BufferedImage.TYPE_BYTE_INDEXED,icm);

		return image;
	}
	public static BufferedImage create2ColorImage(double width, double height, Color[] colors)
	{
		Color c0 = colors[0];
		Color c1 = colors[1];

		byte[] red = new byte[]{ (byte) c0.getRed(), (byte) c1.getRed() };
		byte[] green = new byte[]{ (byte) c0.getGreen(), (byte) c1.getGreen() };
		byte[] blue = new byte[]{ (byte) c0.getBlue(), (byte) c1.getBlue() };

		IndexColorModel icm = new IndexColorModel(1, 2, red, green, blue);
		BufferedImage image = 
				new BufferedImage((int) width,(int) height,BufferedImage.TYPE_BYTE_INDEXED,icm);

		return image;
	}
	public static BufferedImage scaleImage(
			BufferedImage image, double xs, double ys, RenderingHints hints
			)
	{
		AffineTransform t = AffineTransform.getScaleInstance(xs, ys);
		AffineTransformOp scaleOP = new AffineTransformOp(t,GraphicsConfig.speedImageHints());
		return scaleOP.filter(image, null);
	}
	public static BufferedImage scaleImageToSize(
			BufferedImage image, double width, double height, RenderingHints hints
			)
	{
		if ( width == image.getWidth() && height == image.getHeight() )
			return image;

		double xs =  width/image.getWidth(),
			ys =  height/image.getHeight();
		double s = Math.min(xs,ys);
//		System.out.println("image  w "+image.getWidth()+" h "+image.getHeight());
//		System.out.println("    scale xs "+xs+" ys "+ys+" s "+s);
		AffineTransform t = AffineTransform.getScaleInstance(s,s);
		AffineTransformOp scaleOP = new AffineTransformOp(t,hints);
		return scaleOP.filter(image, null);
	}
	public static BufferedImage copyImage(BufferedImage image)
	{
		BufferedImage copy = createImage(image.getWidth(),image.getHeight(),
				image.getTransparency());
		Graphics2D g2d = (Graphics2D) copy.getGraphics();
		g2d.drawImage(image,0,0,null);
		return copy;
	}
	public static AffineTransform createTransformScaleToScreenDPI(
			double sourceDPI
			)
	{
		float scale = Toolkit.getDefaultToolkit().getScreenResolution() / 
				(float) sourceDPI;
		return AffineTransform.getScaleInstance(scale,scale);
	}
	public static void fillImage(BufferedImage image, Color color)
	{
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setPaint(color);
		g2d.fill(new Rectangle(0,0,image.getWidth(),image.getHeight()));
		g2d.dispose();
	}
	public static boolean isValidImageWriter(String formatName) {
		for(String fmt : ImageIO.getWriterFormatNames())
			if ( fmt.equalsIgnoreCase(formatName) ) return true;
		return false;
	}
	public static BufferedImage openImage()
	{
		return openImage("GraphicsUtil");
	}
	public static BufferedImage openImage(String appContext)
	{
		BufferedImage image = null;
		try {
			HFileChooser chooser = new HFileChooser(appContext);
			chooser.setExtensionFilters(new String[]{"gif","jpg","png","bmp"});

			int returnState = chooser.showOpenDialog(new JWindow());

			if ( returnState == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getFilteredSelection();
				image = ImageIO.read(new FileImageInputStream(
						file));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return image;
	}
	public static BufferedImage readImage(String path) 
		throws IOException,FileNotFoundException
	{
		File file = new File(path);
		return readImage(file);
	}
	public static BufferedImage readImage(File file) 
		throws IOException,FileNotFoundException
	{
		if ( file == null ) 
			throw new NullPointerException( "file arg is null");

		if ( !file.exists() ) 
			throw new FileNotFoundException("File does not exist");

		FileImageInputStream inStream = new FileImageInputStream(file);
		BufferedImage image = ImageIO.read(inStream);
		if ( image == null ) {
			inStream.close();
			throw new RuntimeException( "Unable to read image input file: "
				+file.getPath());
		}
		
		return image;
	}
	public static boolean writeImageByExtension(
			File file, 
			BufferedImage image, 
			String formatName
			) 
		throws IOException
	{
		boolean r = true;

		try {
			if ( file == null ) 
				throw new NullPointerException( "file arg is null");

			if ( image == null ) 
				throw new NullPointerException( "Image arg is null");
			
			if ( formatName == null )
				throw new NullPointerException( "Format arg is null");
			
			if ( !isValidImageWriter(formatName) ) 
				throw new IllegalArgumentException( "Unsupported image format: '"+formatName+"'");
			if ( ImageWriterInfo.stripAlpha(formatName) &&
					image.getTransparency() != Transparency.OPAQUE ) {
					BufferedImage noAlphaImage = createImage(image.getWidth(),
							image.getHeight(), Transparency.OPAQUE);
					Graphics2D g2 = (Graphics2D) noAlphaImage.getGraphics();
					g2.drawImage(image,0,0,null);
					image = noAlphaImage;
			}
			ImageOutputStream outStream = ImageIO.createImageOutputStream(file);
//			System.out.println("formatName "+formatName+" file "+file.getPath());
			r = ImageIO.write(image,formatName,outStream);
//			System.out.println("write image result: "+r);
		} catch(IOException e) {
			r = false;
			throw e;
//		} catch(Exception e) {
//			r = false;
//			throw e;
		}
		return r;
//		return ImageIO.write(image,formatExtension, new FileImageOutputStream(file));
	}
	public static int saveImage(BufferedImage image)
	{
		return saveImage(image,"GraphicsUtil");
	}
	public static int saveImage(BufferedImage image, String appContext)
	{
		int returnState = 0;
		try {
			HFileChooser chooser = new HFileChooser(appContext);
			chooser.setExtensionFilters(new String[]{"gif","jpg","png","bmp"});

			returnState = chooser.showSaveDialog(new JWindow());

			if ( returnState == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getFilteredSelection();
				String ext = chooser.parseExtension(file);
				ImageIO.write(image,ext,new FileImageOutputStream(
						file));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return returnState;
	}
	public static BufferedImage drawFilledTextOnImage(String text, Font font, 
			Color foreColor, Color fillColor, double imageWidth,
			double imageHeight) 
	{
		if ( font == null ) font = GraphicsConfig.DEFAULT_FONT;

		BufferedImage image = ImageUtils.createTransparentImage(imageWidth, imageHeight);

		Graphics2D g2 = (Graphics2D) image.getGraphics();
		TextLayout layout = new TextLayout(text,
				font,
				GraphicsConfig.DEFAULT_FRC
				);
		Shape outline = layout.getOutline(null);
		Rectangle2D bounds = outline.getBounds2D();

		AffineTransform transform = new AffineTransform();

		transform.translate(imageWidth/2,imageHeight/2);

		double scale = 0.;
		if ( bounds.getWidth() > bounds.getHeight() ) scale = imageWidth/bounds.getWidth();
		else scale = imageHeight/bounds.getHeight();
		transform.scale(scale,scale);

		transform.translate(-bounds.getCenterX(),-bounds.getCenterY());

		outline = transform.createTransformedShape(outline);


		g2.setPaint(fillColor);
		g2.fill(outline);
		g2.setPaint(foreColor);
		g2.draw(outline);

		return image;
	}
	public static BufferedImage rotateImage90(BufferedImage image,
			int transparency) 
	{
		int w = image.getWidth(), h = image.getHeight();
		BufferedImage rotatedImage = createImage(h,w,transparency);
		RenderingHints hints = GraphicsConfig.qualityImageHints();

		Graphics2D g2 = (Graphics2D) rotatedImage.getGraphics();

		g2.translate(h,0);
		g2.rotate(Math.PI/2);
		g2.setRenderingHints(hints);
		g2.drawImage(image,0,0,null);

		return rotatedImage;
	}
	public static ImageIcon shapeToIcon(Shape shape, Color foreColor, 
			double width, double height, boolean fill)
	{
		BufferedImage image = ImageUtils.createTransparentImage(
				width,height);

		Graphics2D g2 = (Graphics2D) image.getGraphics();
		GraphicsConfig.smoothRenderingON(g2);
		if ( fill ) {
			g2.setPaint(foreColor);
			g2.fill(shape);
		}
		g2.setStroke(new BasicStroke(1f));
		g2.draw(shape);

		g2.dispose();

		return new ImageIcon(image);
	}
	public static ImageIcon resourceToIcon(Class source, String path) 
	{
		Objects.requireNonNull(path);

		InputStream stream = source.getResourceAsStream(path);
		if ( stream == null ) return null;

		return streamToIcon(stream,null);
	}
	public static ImageIcon fileToIcon(String path) 
	{
		Objects.requireNonNull(path);

		return fileToIcon(new File(path));
	}
	public static ImageIcon fileToIcon(File file) 
	{
		Objects.requireNonNull(file);

		if ( !file.exists() ) return null;

		try {
			return streamToIcon(new FileInputStream(file),null);
		} catch(IOException e) {
			return null;
		}
	}
	public static ImageIcon streamToIcon(InputStream stream,
			Dimension size) 
	{
		Objects.requireNonNull(stream);

		BufferedImage image = null;

		try {
			image = ImageIO.read(stream);
			if ( image == null ) {
				stream.close();
				return null;
			}
		} catch(IOException e) {
			return null;
		} 
		if ( size != null ) image = ImageUtils.scaleImageToSize(image,
			size.width,size.height, GraphicsConfig.qualityImageHints());

		return new ImageIcon(image);
	}
	public static void draw(Graphics g, BufferedImage image) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHints(GraphicsConfig.qualityImageHints());
		g2.drawImage(image,0,0,null);
		g2.dispose();
	}
}
