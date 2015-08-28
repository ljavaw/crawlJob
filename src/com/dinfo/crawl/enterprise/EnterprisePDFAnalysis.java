package com.dinfo.crawl.enterprise;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class EnterprisePDFAnalysis {

	public static void main(String[] args) {
		String pdfPath = "F:/600717_20150813_1.pdf";
		EnterprisePDFAnalysis test = new EnterprisePDFAnalysis();
		
		Map<String, String> result = test.parsePdf(pdfPath);
		String text = result.get("text");
		System.out.println("text=="+text);
	}
	
	public Map<String, String> parsePdf(String pdfPath){
		
		Map<String, String> result = new HashMap<String, String>();
		try {
			PDDocument doc = PDDocument.load(pdfPath);
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);
			result.put("text", text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
