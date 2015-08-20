package com.dinfo.crawl.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement
public class Fetcher implements Serializable{
	@XmlElement(name="type")
	private String fetcherType;
	
	@XmlElement(name="isusejs")
	private String fetcherIsusejs;
	
	@XmlElement(name="reqmethod")
	private String reqmethod;
	
	
	
	public String getReqmethod() {
		return reqmethod;
	}

	public void setReqmethod(String reqmethod) {
		this.reqmethod = reqmethod;
	}

	public Fetcher() {
		super();
	}

	public Fetcher(String fetcherType) {
		super();
		this.fetcherType = fetcherType;
	}

	public String getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(String fetcherType) {
		this.fetcherType = fetcherType;
	}

	public String getFetcherIsusejs() {
		return fetcherIsusejs;
	}

	public void setFetcherIsusejs(String fetcherIsusejs) {
		this.fetcherIsusejs = fetcherIsusejs;
	}

	
	
	
}
