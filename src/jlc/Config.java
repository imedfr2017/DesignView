package jlc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Config {
	final static Font FONT = new Font("Sans Serif",Font.PLAIN,18);
	final static Border BLACK_BORDER = 
			BorderFactory.createLineBorder(Color.BLACK);
	final static Border HILIGHT_BORDER = 
			BorderFactory.createLineBorder(Color.CYAN,2);
	final static Border INSET_BORDER = 
			BorderFactory.createEmptyBorder(10,10,10,10);
	final static Color DEFAULT_BACKGROUND = Color.GRAY;
	final static Color PANEL_BACKGROUND = Color.LIGHT_GRAY;
	final static Color LABEL_BACKGROUND = Color.LIGHT_GRAY;
	final static Color GADGET_BACKGROUND = Color.LIGHT_GRAY;
	final static Insets GADGET_INSETS = new Insets(0,0,0,0);
	final static Dimension GADGET_SIZE = new Dimension(30,30);
	final static Float DEFAULT_STROKEWIDTH = 1f;
}
