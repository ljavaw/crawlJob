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
public class Subexpress implements Serializable{
	
	@XmlAttribute(name="filed")
	private String filed;
	
	@XmlValue
	//@XmlJavaTypeAdapter(value=CDATAAdapter.class)
	private String subexpress;
	
	public String getFiled() {
		return filed;
	}
	public void setFiled(String filed) {
		this.filed = filed;
	}
	public String getSubexpress() {
		return subexpress.substring(9,subexpress.length()-4);
	}
	public void setSubexpress(String subexpress) {
		this.subexpress = subexpress;
	}
	
	
}
