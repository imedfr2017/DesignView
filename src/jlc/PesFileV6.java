package jlc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class PesFileV6 
{
	private final static String EXT = DesignParser.FORMAT.PESV6.EXT;
	private final static String TYPE = DesignParser.FORMAT.PESV6.TYPE;
	private final static int PEC_START_POS = 8;
	private final static int COLORNAME_RGB_TABLE_COUNT_POS = 94;
	private final static int COLORNAME_RGB_TABLE_POS = 100;
	private Design design;


	public PesFileV6(FilePack filePack) 
		throws Exception
	{
		Objects.requireNonNull(filePack);

		ByteBuffer buffer = filePack.getFileBuffer();

		buffer.order(ByteOrder.LITTLE_ENDIAN);

		// pec code block start

		buffer.position(PEC_START_POS);
		int pecStart = buffer.getInt();

		// colors

		buffer.position(COLORNAME_RGB_TABLE_COUNT_POS);
		int numColors = buffer.get();
//		System.out.println("RGB table num colors "+numColors);
		Color[] colorTable = new Color[numColors];
		String[] colorNames = new String[numColors];

		buffer.position(COLORNAME_RGB_TABLE_POS);

		for(int i=0; i<numColors; i++) {
			int r = readUByte(buffer);
			int g = readUByte(buffer);
			int b = readUByte(buffer);
//			System.out.println(
//					" r "+r
//					+" g "+g
//					+" b "+b
//					);
			colorTable[i] = new Color(r,g,b);

			buffer.position(buffer.position()+5);
			int nameLen = readUByte(buffer);

			byte[] nameBytes = new byte[nameLen];
			buffer.get(nameBytes);

			colorNames[i] = new String(nameBytes);

			int skipManuName = readUByte(buffer);
			buffer.position(buffer.position()+skipManuName);

			buffer.position(buffer.position()+1);

			int skipUnk = readUByte(buffer);
			buffer.position(buffer.position()+skipUnk);

//			System.out.println(
//					"color "+colorNames[i]
//					+" "+colorTable[i]
//					);
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

		design = new PesDesign(filePack.getName(),EXT,TYPE,width,height);
		design.setColor(colorTable[0],colorNames[0]);

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
				design.setColor(colorTable[colorIndex],colorNames[colorIndex]);
				colorIndex++;
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
	public Design getDesign() {
		return design;
	}
	private int readUByte(ByteBuffer buff) {
		byte b = buff.get();
		int i = b & 0xff;
		return i;
	}
}
