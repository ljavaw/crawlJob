package com.dinfo.crawl.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Parse implements Serializable{
	
	@XmlElements(value = {@XmlElement(type=Parameter.class, name="parameter")})
	private List<Parameter> paraList;

	public List<Parameter> getParaList() {
		return paraList;
	}

	public void setParaList(List<Parameter> paraList) {
		this.paraList = paraList;
	}
	
	
}
