package com.samsonan.lucene_experiments;

import java.io.File;
import java.io.FileFilter;

public class TextFileFilter implements FileFilter {

	public boolean accept(File filename) {
		return filename.getName().toLowerCase().endsWith(".txt");
	}

   
}