package org.zl.jf;

import java.util.Hashtable;

public  class UploadMonitor {

	static Hashtable uploadTable = new Hashtable();

	static void set(String fName, UplInfo info) {
		uploadTable.put(fName, info);
	}

	static void remove(String fName) {
		uploadTable.remove(fName);
	}

	static UplInfo getInfo(String fName) {
		UplInfo info = (UplInfo) uploadTable.get(fName);
		return info;
	}
}
