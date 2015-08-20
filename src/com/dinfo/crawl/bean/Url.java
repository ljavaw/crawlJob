package com.dinfo.crawl.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement
public class Url implements Serializable{
	
	@XmlValue
	//@XmlJavaTypeAdapter(value=CDATAAdapter.class)
	private String urlStr;
	
	@XmlAttribute(name="count")
	private String count;
	
	@XmlAttribute(name="param0")
	private String param0;
	
	@XmlAttribute(name="param1")
	private String param1;
	
	@XmlAttribute(name="param2")
	private String param2;
	
	@XmlAttribute(name="param3")
	private String param3;
	
	
	

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getParam0() {
		return param0;
	}

	public void setParam0(String param0) {
		this.param0 = param0;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}
	
	
}
