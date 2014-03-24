package org.zols.templatemanager.util;

import java.io.File;
import java.io.FilenameFilter;

public class GenericExtFilter implements FilenameFilter {

	private String ext;

	public GenericExtFilter(String ext) {
		this.ext = ext;
	}

	public String getExt() {
		return ext;
	}

	public boolean accept(File dir, String name) {
		return ((!dir.getName().equals("mobile") && !dir.getName().equals(
				"tablet")) && (name.endsWith(ext) || new File(
				dir.getAbsolutePath() + File.separator + name).isDirectory()));
	}
}
