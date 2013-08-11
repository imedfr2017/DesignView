package jlc;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import jlc.Design;
import jlc.PesDesign;
import jlc.DesignPack;
import jlc.FilePacks;

public class PesFileV1 
{
	private final static String EXT = DesignParser.FORMAT.PESV6.EXT;
	private final static String TYPE = DesignParser.FORMAT.PESV6.TYPE;
	private final static int PEC_START_POS = 8;
	private int[] colorIndexes;
	private PesDesign design;


	public PesFileV1(FilePack filePack) 
		throws IOException
	{
		Objects.requireNonNull(filePack);

		ByteBuffer buffer = filePack.getFileBuffer();

		buffer.order(ByteOrder.LITTLE_ENDIAN);

		// pec code block start

		buffer.position(PEC_START_POS);
		int pecStart = buffer.getInt();
//		System.out.println("pecstart "+pecStart);

		// colors

		buffer.position(pecStart+48);
		int numColors = buffer.get() + 1;
//		System.out.println("num colors "+numColors);

		colorIndexes = new int[numColors];

		buffer.position(pecStart+49);

		for(int i=0; i<numColors; i++) {
			colorIndexes[i] = buffer.get() & 0x000000ff;
		}

		buffer.position(pecStart+514);
		int graphOffset = buffer.getShort();

		// design size

		buffer.position(pecStart+520);
		int width = buffer.getShort();
		int height = buffer.getShort();

		int pecBeg = pecStart + 532;

		buffer.position(pecBeg);

		int colorIndex = 0;
//		Color color = colors[colorIndexes[colorIndex]];
//		Color color = colorTable[colorIndex];

		design = new PesDesign(filePack.getName(),EXT,TYPE,width,height);
		design.setColor(colors[colorIndexes[0]],"unknown");

		while( buffer.position() < buffer.capacity() ) {

			int val1 = readUByte(buffer);
			int val2 = readUByte(buffer);

			boolean jumpstitch = false;

			// end of pec
			if ( val1 == 255 && val2 == 0 ) break;

			// end of color/thread block of stitches.
			// set color to next color.
			// skip a byte.
			if ( val1 == 254 && val2 == 176 ) {	// FEB0
				design.setColor(colors[colorIndexes[colorIndex]],"unknown");
				colorIndex++;
//				design.setColor(color);
//				color = colorTable[colorIndex++];
//				color = colors[colorIndexes[colorIndex++]];
				buffer.get();
				continue;
			}

			// actual stitch coordinates

			// x
			if ( (val1 & 128 ) != 0 ) {
//				val1 = ((val1 & 15) << 8) + val2;
				val1 = val1 & 15;
				val1 = val1 << 8;
				val1 = val1 + val2;
				if ( (val1 & 2048) != 0 ) val1 -= 4096;
				val2 = readUByte(buffer);
				jumpstitch = true;
			} else {
				if ( (val1 & 64) != 0 ) val1 -= 128;
			}

			// y
			if ( (val2 & 128) != 0 ) {
				val2 = ((val2 & 15) << 8) + readUByte(buffer);
				if ( (val2 & 2048) != 0 ) val2 -= 4096;
				jumpstitch = true;
			} else {
				if ( (val2 & 64) != 0 ) val2 -= 128;
			}

			if ( jumpstitch ) design.jumpTo(val1,val2);
			else design.stitchTo(val1,val2);
		}
	}
	public PesDesign getDesign() {
		return design;
	}
	private int readUByte(ByteBuffer buff) {
		byte b = buff.get();
		int i = b & 0xff;
		return i;
	}
	private final static Color[] colors = {
		new Color( 0,  0, 124 ),
		new Color( 14,  31, 124 ),
		new Color( 10,  85, 163 ),
		new Color( 48, 135, 119 ),
		new Color( 75, 107, 175 ),
		new Color(237,  23,  31 ),
		new Color(209,  92,   0 ),
		new Color(145,  54, 151 ),
		new Color(228, 154, 203 ),
		new Color(145,  95, 172 ),
		new Color(	157, 214, 125 ),
		new Color(	232, 169,   0 ),
		new Color(	254, 186,  53 ),
		new Color(	255, 255,   0 ),
		new Color(	112, 188,  31 ),
		new Color(	192, 148,   0 ),
		new Color(	168, 168, 168 ),
		new Color(	123, 111,   0 ),
		new Color(	255, 255, 179 ),
		new Color(	 79,  85,  86 ),
		new Color(  0,   0,   0 ),
		new Color(	 11,  61, 145 ),
		new Color(	119,   1, 118 ),
		new Color(	 41,  49,  51 ),
		new Color(	 42,  19,   1 ),
		new Color(	246,  74, 138 ),
		new Color(	178, 118,  36 ),
		new Color(	252, 187, 196 ),
		new Color(	254,  55,  15 ),
		new Color(240, 240, 240 ),
		new Color(	106,  28, 138 ),
		new Color(	168, 221, 196 ),
		new Color(	 37, 132, 187 ),
		new Color(	254, 179,  67 ),
		new Color(	255, 240, 141 ),
		new Color(	208, 166,  96 ),
		new Color(	209,  84,   0 ),
		new Color(	102, 186,  73 ),
		new Color(	 19,  74,  70 ),
		new Color(	135, 135, 135 ),
		new Color(	216, 202, 198 ),
		new Color(	 67,  86,   7 ),
		new Color(	254, 227, 197 ),
		new Color(	249, 147, 188 ),
		new Color(	  0,  56,  34 ),
		new Color(	178, 175, 212 ),
		new Color(	104, 106, 176 ),
		new Color(	239, 227, 185 ),
		new Color(	247,  56, 102 ),
		new Color(	181,  76, 100 ),
		new Color(	 19,  43,  26 ),
		new Color(	199,   1,  85 ),
		new Color(	254, 158,  50 ),
		new Color(	168, 222, 235 ),
		new Color(	  0, 103,  26 ),
		new Color(	 78,  41, 144 ),
		new Color(	 47, 126,  32 ),
		new Color(	253, 217, 222 ),
		new Color(	255, 217,  17 ),
		new Color(	  9,  91, 166 ),
		new Color(	240, 249, 112 ),
		new Color(	227, 243,  91 ),
		new Color(	255, 200, 100 ),
		new Color(	255, 200, 150 ),
		new Color(	255, 200, 200 ),
			};
}
