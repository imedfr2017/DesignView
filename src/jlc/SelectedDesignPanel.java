package jlc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

public class SelectedDesignPanel extends JPanel 
{
	private final Font font = Config.FONT;
	private final Border BLACK_BORDER = Config.BLACK_BORDER;
	private final Border INSET_BORDER = Config.INSET_BORDER;
	private final Color PANEL_BACKGROUND = Config.PANEL_BACKGROUND;
	private final Color LABEL_BACKGROUND = Config.LABEL_BACKGROUND;
	private final Insets GADGET_INSETS = Config.GADGET_INSETS;
	private final Dimension GADGET_SIZE = Config.GADGET_SIZE;
	private final Float DEFAULT_STROKEWIDTH = Config.DEFAULT_STROKEWIDTH;

	private final Color STROKE_GADGET_BACKGROUND = Config.GADGET_BACKGROUND;

	private StitchesPanel stitchesPanel;
	private ThreadsPanel threadsPanel;
	private DesignPack designPack;
	private String filename = "";
	private JTextField filename_TF;
	private JLabel hoopSize_L;

	public SelectedDesignPanel(
			) 
	{
		setLayout(new MigLayout("fill"));

		setBorder(BLACK_BORDER);
		setBackground(PANEL_BACKGROUND);

		stitchesPanel = new StitchesPanel(
				designPack == null ? null : designPack.getDesign(),
				PANEL_BACKGROUND,
				createStroke(DEFAULT_STROKEWIDTH)
				);
		stitchesPanel.setBorder(INSET_BORDER);

		JPanel controls_PNL = new JPanel(new MigLayout("fill"));
		controls_PNL.setBackground(PANEL_BACKGROUND);
		controls_PNL.setBorder(BLACK_BORDER);
		controls_PNL.add(new BgColorSliderPanel(stitchesPanel),
				"dock west");
		controls_PNL.add(createStrokesPanel(),"dock east");

		add(controls_PNL,"dock north");
		add(stitchesPanel,"grow,push,wrap");

		add(createInfoPanel(),"dock south");

		threadsPanel = new ThreadsPanel(stitchesPanel);
		add(threadsPanel,"dock east");

//		JButton machine_B = new JButton("mach");
//		machine_B.addActionListener(new ActionListener() {
//			@Override public void actionPerformed(ActionEvent e) {
//				new StitchMachineFrame(designPack.getDesign());
//			}
//		});
//		add(machine_B);
	}

	private JPanel createBackgroundsPanel()
	{
		final JPanel bgControl_PNL = new JPanel(new MigLayout(
				"","[]0[]0[]"
				));
		bgControl_PNL.setBackground(PANEL_BACKGROUND);

		final ActionListener setBG = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				JComponent b = (JComponent) e.getSource();
				stitchesPanel.setBackground(b.getBackground());
				repaint();
			}
		};

		final JButton dark_B = new JButton();
		dark_B.addActionListener(setBG);
		dark_B.setBackground(Color.BLACK);
		forceButtonSize(dark_B);

		bgControl_PNL.add(dark_B);
		
		final JButton neutral_B = new JButton();
		neutral_B.addActionListener(setBG);
		neutral_B.setBackground(Color.GRAY);
		forceButtonSize(neutral_B);

		bgControl_PNL.add(neutral_B);
		
		final JButton light_B = new JButton();
		light_B.addActionListener(setBG);
		light_B.setBackground(Color.LIGHT_GRAY);
		forceButtonSize(light_B);

		bgControl_PNL.add(light_B);

		return bgControl_PNL;
	}
	private void forceButtonSize(JButton button) {
		button.setMargin(GADGET_INSETS);
		button.setMinimumSize(GADGET_SIZE);
		button.setMaximumSize(GADGET_SIZE);
	}
	//
	// Stroke thickness gadgets
	//
	private JPanel createStrokesPanel() 
	{
		final JPanel strokesControl_PNL = new JPanel(new MigLayout(
				"","[]0[]0[]"
				));
		strokesControl_PNL.setBackground(PANEL_BACKGROUND);

		final class StrokeIcon extends ImageIcon {
			private Stroke stroke;
			public StrokeIcon(float strokeWidth) {
				BufferedImage image = new BufferedImage(
						GADGET_SIZE.width,GADGET_SIZE.height,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = (Graphics2D) image.createGraphics();
				g2.setPaint(STROKE_GADGET_BACKGROUND);
				g2.fill(new Rectangle(0,0,image.getWidth(),image.getHeight()));
				g2.setColor(Color.BLACK);
				stroke = createStroke(strokeWidth);
				g2.setStroke(stroke);
				int x = GADGET_SIZE.width/2, y1 = 5, y2 = GADGET_SIZE.height - 5;
				g2.drawLine(x,y1,x,y2);
				setImage(image);
			}
			private Stroke getStroke() {
				return stroke;
			}
		}

		ActionListener setStroke = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				StrokeIcon si = (StrokeIcon)((JButton) e.getSource()).getIcon();
				Stroke s = si.getStroke();
				stitchesPanel.setStroke(s);
				repaint();
			}
		};

		JButton thin_B = new JButton(new StrokeIcon(1f));
		thin_B.addActionListener(setStroke);
		thin_B.setBackground(STROKE_GADGET_BACKGROUND);
		forceButtonSize(thin_B);

		strokesControl_PNL.add(thin_B);

		JButton medium_B = new JButton(new StrokeIcon(2f));
		medium_B.addActionListener(setStroke);
		medium_B.setBackground(STROKE_GADGET_BACKGROUND);
		forceButtonSize(medium_B);

		strokesControl_PNL.add(medium_B);

		JButton thick_B = new JButton(new StrokeIcon(3f));
		thick_B.addActionListener(setStroke);
		thick_B.setBackground(STROKE_GADGET_BACKGROUND);
		forceButtonSize(thick_B);

		strokesControl_PNL.add(thick_B);

		return strokesControl_PNL;
	}
	private JPanel createInfoPanel() 
	{
		final JPanel info_PNL = new JPanel(new MigLayout("fill"));
		info_PNL.setBackground(PANEL_BACKGROUND);
		info_PNL.setBorder(BLACK_BORDER);

		if ( designPack != null ) filename = designPack.getFilename();

		filename_TF = new JTextField(filename);
		filename_TF.setEditable(false);
		filename_TF.setBackground(LABEL_BACKGROUND);
		filename_TF.setBorder(BLACK_BORDER);
		filename_TF.setHorizontalAlignment(SwingConstants.CENTER);
		filename_TF.setFont(font);

		info_PNL.add(filename_TF,"left,growx");
		hoopSize_L = new JLabel("");
		info_PNL.add(hoopSize_L,"right,wrap");

		return info_PNL;
	}
	private Stroke createStroke(float strokeWidth) {
		return new BasicStroke(
					strokeWidth,
					BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND
				);
	}
	public DesignPack getDesignPack() 
	{
		return designPack;
	}
	public void setDesignPack(DesignPack designPack) 
	{
		if ( designPack != null ) 
		{
			this.designPack = designPack;

			filename_TF.setText(designPack.getFilename());

			setInfo(designPack);

			stitchesPanel.setDesign(designPack.getDesign());
			threadsPanel.setThreadRun(designPack.getDesign().getThreadRuns());
		} else {
			stitchesPanel.setDesign(null);
			threadsPanel.setThreadRun(null);
			filename_TF.setText("");
		}
		repaint();

		return;
	}
	private void setInfo(DesignPack designPack) 
	{
		if ( designPack == null || designPack.getDesign() == null ) {
			hoopSize_L.setVisible(false);
			return;
		}

		Design design = designPack.getDesign();
		float widthInch = design.getWidth()/10f/25.4f;
		float heightInch = design.getHeight()/10f/25.4f;

		String hoopSize_TXT = 
				String.format("(%1$3.2f\" X %2$3.2f\")",widthInch,heightInch)
				;
		hoopSize_L.setHorizontalAlignment(SwingConstants.CENTER);
		hoopSize_L.setText(hoopSize_TXT);
		hoopSize_L.setFont(font);
		hoopSize_L.setVisible(true);
	}
}
