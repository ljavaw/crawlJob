package com.dinfo.crawl.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement
public class Page implements Serializable{
	@XmlElement
	private Urls urls;
	@XmlElement
	private Fetcher fetcher;
	@XmlElement
	private Parse parse;
	
	public Urls getUrls() {
		return urls;
	}

	public void setUrls(Urls urls) {
		this.urls = urls;
	}
	public Fetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(Fetcher fetcher) {
		this.fetcher = fetcher;
	}

	public Parse getParse() {
		return parse;
	}

	public void setParse(Parse parse) {
		this.parse = parse;
	}
	
	

}
