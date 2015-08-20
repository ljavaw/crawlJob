package com.dinfo.fetcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ParseProperty {
	private static Properties p = new Properties();
	static{
		File file = new File("crawl.properties");
		try {
			FileInputStream fin = new FileInputStream(file);
			p.load(fin);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getVaule(String key){
		String value = p.getProperty(key);
		return value;
	}
	
	public static void main(String[] args) {
		String ss = getVaule("firefoxpath");
		System.out.println(ss);
	}

}
