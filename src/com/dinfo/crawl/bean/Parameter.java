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
public class Parameter implements Serializable{
	
	@XmlElement(name="type")
	private String paraType;
	
	@XmlElement(name="fileds")
	private String paraFileds;
	
	@XmlElement(name="expression")
	private String paraExpression;
	
	@XmlElements(value = {@XmlElement(type=Subexpress.class, name="subexpress", required=false)})
	private List<Subexpress> paraSubexpress;
	
	@XmlElement(name="isnextpara", required=false)	
	private String paraIsnextpara;
	
	@XmlElement(name="nextpara", required=false)
	private String paraNextpara;
	
	@XmlElement(name="issave", required=false)
	private String paraIssave;
	
	@XmlElement(name="savepara", required=false)
	private String paraSavepara;
	
	
	public String getParaType() {
		return paraType;
	}

	public void setParaType(String paraType) {
		this.paraType = paraType;
	}
	
	public String getParaFileds() {
		return paraFileds;
	}

	public void setParaFileds(String paraFileds) {
		this.paraFileds = paraFileds;
	}
	
	public String getParaExpression() {
		return paraExpression;
	}
	
	public void setParaExpression(String paraExpression) {
		this.paraExpression = paraExpression;
	}
	
	public List<Subexpress> getParaSubexpress() {
		return paraSubexpress;
	}

	public void setParaSubexpress(List<Subexpress> paraSubexpress) {
		this.paraSubexpress = paraSubexpress;
	}

	public String getParaIsnextpara() {
		return paraIsnextpara;
	}

	public void setParaIsnextpara(String paraIsnextpara) {
		this.paraIsnextpara = paraIsnextpara;
	}
	
	public String getParaNextpara() {
		return paraNextpara;
	}

	public void setParaNextpara(String paraNextpara) {
		this.paraNextpara = paraNextpara;
	}

	public String getParaIssave() {
		return paraIssave;
	}

	public void setParaIssave(String paraIssave) {
		this.paraIssave = paraIssave;
	}

	public String getParaSavepara() {
		return paraSavepara;
	}

	public void setParaSavepara(String paraSavepara) {
		this.paraSavepara = paraSavepara;
	}
	
	

}
