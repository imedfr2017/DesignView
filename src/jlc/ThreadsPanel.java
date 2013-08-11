package jlc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

public class ThreadsPanel extends JPanel 
{
	private final Color PANEL_BACKGROUND = Config.PANEL_BACKGROUND;
	private final Color GADGET_BACKGROUND = Config.GADGET_BACKGROUND;
	private final Insets GADGET_INSETS = Config.GADGET_INSETS;

	private final Border border = 
			BorderFactory.createLineBorder(Color.BLACK);
	private final Color DIM_THREAD_COLOR = Color.LIGHT_GRAY;
	private final Dimension COLOR_ICON_SIZE = new Dimension(20,20);
	private JPanel gadgetPanel;
	private StitchesPanel stitchesPanel;

	public ThreadsPanel(StitchesPanel stitchesPanel)
	{
		this.stitchesPanel = Objects.requireNonNull(stitchesPanel);

		setLayout(new MigLayout("fill"));

		setBorder(border);
		setBackground(PANEL_BACKGROUND);
	}
	public void setThreadRun(ArrayList<ThreadRun> threadRuns) {
		if ( gadgetPanel != null ) {
			remove(gadgetPanel);
		}
		if ( threadRuns == null ) threadRuns = new ArrayList<>();
		gadgetPanel = addThreadGadgets(threadRuns);
		add(gadgetPanel,"grow");
	}
	private JPanel addThreadGadgets(ArrayList<ThreadRun> threadRuns) 
	{
		final class ColorIcon extends ImageIcon {
			public ColorIcon(Color color) {
				BufferedImage image = new BufferedImage(
						COLOR_ICON_SIZE.width,COLOR_ICON_SIZE.height,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = (Graphics2D) image.createGraphics();
				g2.setPaint(color);
				g2.fill(new Rectangle(0,0,image.getWidth(),image.getHeight()));
				setImage(image);
			}
		}

		final JPanel panel = new JPanel(new MigLayout(
				"fillx,gap 0 0,insets 0 0 0"
				));
		panel.setBackground(PANEL_BACKGROUND);
		int prefWidth = 0;
		int index = 0;
		for(ThreadRun run : threadRuns) 
		{
			JPanel gadget = new JPanel(new MigLayout(
					"fill,gap 0 0,insets 0 0 0 0"));
			gadget.setBackground(PANEL_BACKGROUND);

			JButton button = new JButton(new ColorIcon(run.getColor()));
			button.setText(run.getColorName());
			button.setBackground(GADGET_BACKGROUND);
			button.setMargin(GADGET_INSETS);
			button.setHorizontalAlignment(SwingConstants.LEFT);
			gadget.add(button,"wrap,growx");
			prefWidth = Math.max(prefWidth,gadget.getPreferredSize().width);

			final int i = index;

			button.addMouseListener( new MouseAdapter() {
				@Override public void mousePressed(MouseEvent e) {
					stitchesPanel.startAnim(i);
				}
				@Override public void mouseReleased(MouseEvent e) {
					stitchesPanel.stopAnim();
				}
			});
			panel.add(gadget,"wrap,growx");
			index++;
		}

		Dimension prefSize = panel.getPreferredSize();
		panel.setPreferredSize(new Dimension(prefWidth + 10,prefSize.height));

		return panel;
	}
	private Stroke createStroke(float strokeWidth) {
		return new BasicStroke(
					strokeWidth,
					BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND
				);
	}
}
