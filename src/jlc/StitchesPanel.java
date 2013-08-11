package jlc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JPanel;
import util.ImageUtils;
import util.SwingTimers;

public class StitchesPanel extends JPanel
{
	private final static Color DIM_THREAD_COLOR = Color.LIGHT_GRAY;
	private final static int FRAME_INTERVAL = 10;

	private Design design;
	private Stroke stroke;
	private List<Stroke> strokes = new ArrayList<>();
	private List<Color> colors = new ArrayList<>();
	private List<Boolean> animated = new ArrayList<>();
	private ThreadDrawable animatedDrawable;
	private boolean animating;
	private List<ThreadDrawable> threadDrawables = new ArrayList<>();
	private final RenderingHints hints = new RenderingHints(null);
	private BufferedImage image;
	private boolean updateImage = true;
	private Dimension lastDesignSize = new Dimension(0,0);

	public StitchesPanel(
			final Design design,
			final Color imageBg,
			final Stroke stroke
			) 
	{
		setDesign(design);
		Objects.requireNonNull(imageBg);
		this.stroke= Objects.requireNonNull(stroke);

		setBackground(imageBg);
		setStroke(stroke);

		hints.put(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		hints.put(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(
				RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		hints.put(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(
				RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	}
	public void startAnim(int threadNum) {
		if ( animating ) stopAnim();
		animating = true;
		for(int i=0,ie=threadDrawables.size(); i<ie; i++) {
			ThreadDrawable tDraw = threadDrawables.get(i);
			if ( i == threadNum ) {
				tDraw.setAnimate(true);
				animatedDrawable = tDraw;
			} else {
				tDraw.color = DIM_THREAD_COLOR;
			}
		}

		ActionListener timer = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				updateImage = true;
				StitchesPanel.this.repaint();
			}
		};
		SwingTimers.connectTimer(timer, FRAME_INTERVAL);
		SwingTimers.start(timer);
	}
	public void stopAnim() {
		if ( !animating ) return;
		SwingTimers.stopAll();
		animating = false;
		animatedDrawable.setAnimate(false);
		resetColors();
	}
	public void setDesign(Design design) {
		this.design = design;
		if ( design != null ) createThreadDrawables();
		updateImage = true;
	}
	private void createThreadDrawables() 
	{
		ArrayList<ThreadRun> threadRuns = design.getThreadRuns();
		threadDrawables.clear();
		int i = 0;
		for(ThreadRun thread : threadRuns) {
			ThreadDrawable tDraw = new ThreadDrawable(
					thread,
					true,
					thread.getColor(),
					stroke
					);
			threadDrawables.add(tDraw);
		}
	}
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
		for(ThreadDrawable tDraw : threadDrawables) tDraw.stroke = stroke;
		updateImage = true;
	}
	@Override public void setBackground(Color bg) {
		super.setBackground(bg);
		updateImage = true;
	}
	private void resetColors() {
		for(ThreadDrawable tDraw : threadDrawables) 
				tDraw.color = tDraw.thread.getColor();

		updateImage = true;
		repaint();
	}
	@Override protected void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHints(hints);
		Shape clip = g2.getClip();

		g2.setPaint(getBackground());
		g2.fill(clip);

		draw(g2);

		g2.dispose();
	}
	
	private void draw(Graphics2D g2)
	{
		Rectangle bounds = getBounds();

		boolean boundsChange = !lastDesignSize.equals(bounds.getSize()); 
		if ( boundsChange ) 
			image = ImageUtils.createTransparentImage(
					bounds.width, bounds.height
					);

		if ( boundsChange || updateImage ) {
			drawAll(prepareImage());
		}

		lastDesignSize.setSize(bounds.width, bounds.height);

		updateImage = false;

		g2.drawImage(image,0,0,null);
	}
	private Graphics2D prepareImage()
	{
		Graphics2D gImage = (Graphics2D) image.getGraphics();

		gImage.setPaint(getBackground());
		gImage.fillRect(0,0,image.getWidth(),image.getHeight());

		if ( design == null ) return gImage;

		gImage.transform(createTransform());

		return gImage;
	}
	private AffineTransform createTransform() 
	{
		if ( design == null ) return null;

		Rectangle bounds = getBounds();
		Rectangle designBounds = design.getBounds();
		Insets insets = getInsets();

		double width = bounds.getWidth() 
				- insets.right - insets.left;
		double height = bounds.getHeight()
				- insets.top - insets.bottom;
		double designWidth = designBounds.width;
		double designHeight = designBounds.height;

		double min = 0;

		if ( width < height ) min = width;
		else min = height;

		double sx = min/designWidth;
		double sy = min/designHeight;

		double xinset = (width-(designWidth*sx))/2
				+ insets.left;
		double yinset = (height-(designHeight*sy))/2
				+ insets.top;

		AffineTransform transform = new AffineTransform();
		transform.translate(xinset,yinset);
		transform.scale(sx,sy);
		transform.translate(-designBounds.x,-designBounds.y);

		return transform;
	}
	private void drawAll(Graphics2D gImage)
	{
		if ( design == null ) return;

		for(ThreadDrawable tDraw : threadDrawables)
			tDraw.draw(gImage);
	}
	private class ThreadDrawable
	{
		final private ThreadRun thread;
		private Color color;
		private Stroke stroke;

		private boolean visible;
		private boolean animating;
		private int animIndex;

		private ThreadDrawable(ThreadRun thread, Boolean visible, Color displayColor, Stroke stroke) {
			this.thread = thread;
			this.visible = visible;
			this.color = displayColor;
			this.stroke = stroke;
		}
		private void draw(Graphics2D g2) {
			if ( visible ) {
				g2.setColor(color);
				g2.setStroke(stroke);
				ArrayList<Stitch> stitches = thread.getStitchList();
				if ( animating ) {
					List<Stitch> subList = stitches.subList(0,animIndex++);
					if ( animIndex >= stitches.size() ) animating = false;
					for(Stitch s : subList) {
						g2.draw(s.getLine());
					}
				} else {
					for(Stitch s : stitches) {
						g2.draw(s.getLine());
					}
				}
			}
		}
		private void setAnimate(boolean value) {
			if ( animating == value ) return;
			animating = value;
			if ( animating ) {
				animIndex = 0;
				animating = true;
			} else {
				animating = false;
			}
		}
	}
}
