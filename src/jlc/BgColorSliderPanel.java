package jlc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.metal.MetalComboBoxUI;
import net.miginfocom.swing.MigLayout;

public class BgColorSliderPanel extends JPanel
{
	private final Border BLACK_BORDER = Config.BLACK_BORDER;
	private final Color PANEL_BACKGROUND = Config.PANEL_BACKGROUND;
	private final Insets GADGET_INSETS = Config.GADGET_INSETS;
	private final Dimension GADGET_SIZE = Config.GADGET_SIZE;

	private final Dimension PAINTITEM_SIZE = new Dimension(200,25);
	private final float BRIGHTNESS_START = .15f;
	private int gPaintTics = 100;
	private JComponent component;		// component to set background on.
	private JSlider bgSlider;
	private ArrayList<PaintItem> paintItems = new ArrayList<>();
	private PaintItem paintItem;

	public BgColorSliderPanel(final JComponent component)
	{
		this.component = component;

		setLayout(new MigLayout("fill","[]0[]0[]"));

		setBackground(PANEL_BACKGROUND);

		initPaintItems();
		
		bgSlider = new JSlider(
				1,gPaintTics,
				gPaintTics
				);
		bgSlider.setBackground(PANEL_BACKGROUND);
		bgSlider.setPreferredSize(PAINTITEM_SIZE);
		bgSlider.setMinimumSize(PAINTITEM_SIZE);
		bgSlider.setBorder(null);
		bgSlider.setToolTipText("Background Brightness");
		bgSlider.addChangeListener(new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				syncComponentColor();
			}
		});
		syncComponentColor();

		add(bgSlider,"gap,wrap");

		final JComboBox comboBox = createComboBox();
		comboBox.setToolTipText("Choose background color");

		add(comboBox,"growx");
	}
	private void syncComponentColor() {
		int v = bgSlider.getValue();
		component.setBackground(paintItem.getAdjustedColor(v));
	}
	private JComboBox createComboBox() 
	{
		final JComboBox<PaintItem> comboBox = new JComboBox<PaintItem>();
		noButtonComboBox(comboBox);

		class Model extends AbstractListModel<PaintItem>
			implements ComboBoxModel<PaintItem>
		{
			PaintItem selected = paintItems.get(0);

			@Override public int getSize() {
				return paintItems.size();
			}
			@Override public PaintItem getElementAt(int index) {
				return paintItems.get(index);
			}
			@Override public void setSelectedItem(Object anItem) {
				selected = (PaintItem) anItem;
			}
			@Override public Object getSelectedItem() {
				return selected;
			}
		}

		comboBox.setModel(new Model());
		comboBox.setRenderer(new PaintCellRenderer());
		comboBox.setEditable(false);
		comboBox.addItemListener(new ItemListener() {
			@Override public void itemStateChanged(ItemEvent e) {
				if ( e.getStateChange() == ItemEvent.SELECTED ) {
					paintItem = (PaintItem) e.getItem();
					syncComponentColor();
				}
			}
		});

		return comboBox;
	}
	private void noButtonComboBox(JComboBox comboBox) 
	{
		ComboBoxUI ui = comboBox.getUI();
		if ( ui instanceof MetalComboBoxUI ) {
			MetalComboBoxUI mui = new MetalComboBoxUI() {
				@Override protected JButton createArrowButton() {
					JButton emptyJB = new JButton();
					Dimension emptyS = new Dimension(0,0);
					Border emptyB = BorderFactory.createEmptyBorder();
					emptyJB.setPreferredSize(emptyS);
					emptyJB.setMinimumSize(emptyS);
					emptyJB.setMaximumSize(emptyS);
					emptyJB.setBorder(emptyB);
					return emptyJB;
				}
				@Override public void configureArrowButton() {
				}
			};
			comboBox.setUI(mui);
		} else {
			BasicComboBoxUI mui = new BasicComboBoxUI() {
				@Override protected JButton createArrowButton() {
					JButton emptyJB = new JButton();
					Dimension emptyS = new Dimension(0,0);
					Border emptyB = BorderFactory.createEmptyBorder();
					emptyJB.setPreferredSize(emptyS);
					emptyJB.setMinimumSize(emptyS);
					emptyJB.setMaximumSize(emptyS);
					emptyJB.setBorder(emptyB);
					return emptyJB;
				}
				@Override public void configureArrowButton() {
				}
			};
			comboBox.setUI(mui);
		}
	}
	private void initPaintItems() {
		paintItems.clear();
		for(int i=0,ie=COLORS.length; i<ie; i++) 
			paintItems.add(new PaintItem(i,COLORS[i],PAINTITEM_SIZE));
		paintItem = paintItems.get(0);
	}
	final class PaintCellRenderer 
		implements ListCellRenderer<PaintItem>
	{
		@Override public Component getListCellRendererComponent(
				JList<? extends PaintItem> list,
				PaintItem value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
				) 
		{
			return value;
		}
	}
	private class PaintItem extends JPanel 
	{
		private Color color;
		int index;

		public PaintItem(
				int index,
				final Color color, 
				final Dimension size
				)
		{
			this.index = index;
			this.color = color;

			setLayout(new MigLayout());
			setBorder(BLACK_BORDER);
			setPreferredSize(size);
			setMinimumSize(size);
			setOpaque(true);
			setBackground(color);
		}

		@Override protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			Rectangle bounds = g2.getClipBounds();
			g2.setPaint(color);
			g2.fill(bounds);
			g2.dispose();
		}
		
		public Color getColor() {
			return color;
		}
		private Color getAdjustedColor(int brightPer100)
		{
			float[] colorHSB = Color.RGBtoHSB(color.getRed(),color.getGreen(),
					color.getBlue(),null);
			colorHSB[2] = brightPer100/100f;
			int rgb = Color.HSBtoRGB(colorHSB[0],colorHSB[1],colorHSB[2]);
			return new Color(rgb);
		}

		@Override
		public String toString() {
			return "paint item #"+index;
		}
		
	}
	final private static Color[] COLORS = {
		new Color(255,255,255),

		new Color(255,0,0),
		new Color(255,0,125),
		new Color(255,125,0),
		new Color(255,125,125),

		new Color(0,0,255),
		new Color(125,0,255),
		new Color(0,125,255),
		new Color(125,125,255),

		new Color(255,255,0),
		new Color(255,255,125),

		new Color(0,255,255),
		new Color(125,255,255),

		new Color(255,0,255),
		new Color(255,125,255),
		new Color(75,75,0),

	};
}