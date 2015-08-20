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
public class Urls implements Serializable {

	@XmlElement(name = "type")
	private int urlsType;

	@XmlElements(value = { @XmlElement(type = Url.class, name = "url") })
	private List<Url> urlList;

	private String savetype;

	private String table;

	private String columns;

	private String sleeptime;
	private String trowkey;
	private String tfamily;
	private String indextablename;
	private String irowkey;
	private String ifamily;
	private String icolumns;
	private String repeattimes;

	public int getUrlsType() {
		return urlsType;
	}

	public void setUrlsType(int urlsType) {
		this.urlsType = urlsType;
	}

	public List<Url> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<Url> urlList) {
		this.urlList = urlList;
	}

	public String getSavetype() {
		return savetype;
	}

	public void setSavetype(String savetype) {
		this.savetype = savetype;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSleeptime() {
		return sleeptime;
	}

	public void setSleeptime(String sleeptime) {
		this.sleeptime = sleeptime;
	}

	public String getTrowkey() {
		return trowkey;
	}

	public void setTrowkey(String trowkey) {
		this.trowkey = trowkey;
	}

	public String getTfamily() {
		return tfamily;
	}

	public void setTfamily(String tfamily) {
		this.tfamily = tfamily;
	}

	public String getIndextablename() {
		return indextablename;
	}

	public void setIndextablename(String indextablename) {
		this.indextablename = indextablename;
	}

	public String getIrowkey() {
		return irowkey;
	}

	public void setIrowkey(String irowkey) {
		this.irowkey = irowkey;
	}

	public String getIfamily() {
		return ifamily;
	}

	public void setIfamily(String ifamily) {
		this.ifamily = ifamily;
	}

	public String getIcolumns() {
		return icolumns;
	}

	public void setIcolumns(String icolumns) {
		this.icolumns = icolumns;
	}

	public String getRepeattimes() {
		return repeattimes;
	}

	public void setRepeattimes(String repeattimes) {
		this.repeattimes = repeattimes;
	}
	
}
