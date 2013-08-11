package jlc;

import java.io.File;

public class DesignParser {
	public enum FORMAT {
		PESV1("#PES0001","pes","PESV1"),
		PESV6("#PES0060","pes","PESV6"),
		UNKNOWN("","","UNKNOWN")
		;
		String ID, EXT, TYPE;
		private FORMAT(String id, String ext, String type) {
			ID = id;
			EXT = ext;
			TYPE = type;
		}
	}
	public static DesignPack readFile(File designFile) 
		throws Exception
	{
		FilePack filePack = null;
		String name = "";
		String ext = "";
		String type = "";
		Design design = null;

		filePack = FilePacks.create(designFile);
		name = filePack.getName();

		FORMAT fmt = fileFormat(filePack);

		switch(fmt) {
			case PESV1:
				design = new PesFileV1(filePack).getDesign();
				break;
			case PESV6:
				design = new PesFileV6(filePack).getDesign();
				break;
			default:
				throw new Exception(
					"Unknown type of design file - '"+name+"'");
		}

		return new DesignPack(design,filePack);
	}
	private static FORMAT fileFormat(FilePack filePack) 
	{
		for(FORMAT fmt : FORMAT.values()) 
		{
			if ( filePack.getExt().toLowerCase().equals(fmt.EXT) ) {
				byte[] filebytes = filePack.getFileBytes();
				String idFile = new String(filebytes,0,fmt.ID.length());

				if ( idFile.equals(fmt.ID) ) return fmt;
			}
		}
		return FORMAT.UNKNOWN;
	}
}
