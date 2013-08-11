package jlc;

import java.util.Objects;

public class DesignPack
{
	final private Design design;
	final private FilePack filePack;
	private String name;

	public DesignPack(Design design, FilePack filePack) 
	{
		this.design = Objects.requireNonNull(design);
		this.filePack = Objects.requireNonNull(filePack);
	}
	public Design getDesign() {
		return design;
	}
	public FilePack getFilePack() {
		return filePack;
	}
	public String getFilename() {
		return filePack.getName();
	}
	public void setFilename(String name) {
		filePack.setName(name);
	}
	@Override public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.name);
		return hash;
	}
	@Override public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DesignPack other = (DesignPack) obj;
		String otherName = other.getFilename().trim().toLowerCase();
		String thisName = getFilename().trim().toLowerCase();
		if (!thisName.equals(otherName)) return false;
		return true;
	}
	
}
