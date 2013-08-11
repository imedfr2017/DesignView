package util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

class HFileChooserHistory implements Serializable {
	ArrayList<File> historyList = new ArrayList<File>();
	int historyIndex;
}
