package util;

import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageWriterInfo {
	
	private static Map<String,Item> extMap = new HashMap<String,Item>();
	private static Map<String,Item> formatMap = new HashMap<String,Item>();
	private static Set<Item> items;
	static { 
		items = new TreeSet<Item>(
			Arrays.asList(new Item[]{
				new Item("JPEG image file","jpg","jpg",true),
				new Item("GIF image file","gif","gif",false),
				new Item("PNG image file","png","png",false),
				new Item("TIF image file","tif","tif",false),
				new Item("BMP image file","bmp","bmp",false),
		}));
		for(Item item : items) {
			extMap.put(item.ext,item);
			formatMap.put(item.format,item);
		}
	};
	private static class Item implements Comparable<Item> {
		private String desc, ext, format;
		private boolean stripAlpha;
		public Item(String desc, String ext, String format, boolean stripAlpha) {
			this.desc = desc;
			this.ext = ext;
			this.format = format;
			this.stripAlpha = stripAlpha;
		}
		@Override public int compareTo(Item o) {
			if ( o != null ) return o.getDesc().compareTo(desc);
			else return 0;
		}
		public String getDesc() {
			return desc;
		}
//		public int compareTo(String testDesc) {
//			if ( testDesc != null && desc != null )
//				return testDesc.compareTo(desc);
//			else return 0;
//		}
	}
	public static boolean stripAlpha(String ext) {
		if ( extMap.containsKey(ext) ) return extMap.get(ext).stripAlpha;
		else return false;
	}
	public static String[] getAllFormatDescriptions() {
		String[] desc = new String[extMap.size()];
		int i = 0;
		for(Item item : extMap.values()) desc[i++] = getFormatDescription(item.ext);
		return desc;
	}
	public static String getFormatDescription(String ext) {
		if ( extMap.containsKey(ext) ) {
			Item item = extMap.get(ext);
			return item.desc+" - "+item.ext;
		}
		else return null;
	}
}
