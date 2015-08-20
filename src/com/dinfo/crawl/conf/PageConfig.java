package com.dinfo.crawl.conf;

import java.io.Serializable;
import java.util.List;

import com.dinfo.crawl.URLBean;
import com.dinfo.fetcher.bean.CrawlParameter;
import com.dinfo.parse.bean.ParseParameter;

public class PageConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int urlType; // 0:直接取得 1:从上一层的结果url 中取得

	private List<URLBean> urls;  // url

	private CrawlParameter crawlPara;

	private List<ParseParameter> parsePara;

	private int saveType; // 保存类型  0不保存  1保存  2 更改  3 hbase 的入库   4 hbase 带索引表的入库
	
	private String tableName;
	
	private String trowkey;   // hbase 表的rowkey
	 
	private String tfamily;   //hbase 表的列族
	
	private String indexTableName;  //索引表的Name
	
	private String irowkey;   //索引表的rowkey
	
	private String ifamily;   //索引表的列族
	
	private List<String> icolumns;  //索引表的列
	
	private String testurl;  // 检测url
	
	private List<String> columns;
	 
	private long sleepTime;  //睡眠时间

	private String repeattimes;  // 重复次数

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

	public String getIndexTableName() {
		return indexTableName;
	}

	public void setIndexTableName(String indexTableName) {
		this.indexTableName = indexTableName;
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

	public List<String> getIcolumns() {
		return icolumns;
	}

	public void setIcolumns(List<String> icolumns) {
		this.icolumns = icolumns;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTesturl() {
		return testurl;
	}

	public void setTesturl(String testurl) {
		this.testurl = testurl;
	}

	public int getSaveType() {
		return saveType;
	}

	public void setSaveType(int saveType) {
		this.saveType = saveType;
	}

	public int getUrlType() {
		return urlType;
	}

	public void setUrlType(int urlType) {
		this.urlType = urlType;
	}


	public CrawlParameter getCrawlPara() {
		return crawlPara;
	}

	public void setCrawlPara(CrawlParameter crawlPara) {
		this.crawlPara = crawlPara;
	}

	public List<ParseParameter> getParsePara() {
		return parsePara;
	}

	public void setParsePara(List<ParseParameter> parsePara) {
		this.parsePara = parsePara;
	}

	public List<URLBean> getUrls() {
		return urls;
	}

	public void setUrls(List<URLBean> urls) {
		this.urls = urls;
	}

	public String getRepeattimes() {
		return repeattimes;
	}

	public void setRepeattimes(String repeattimes) {
		this.repeattimes = repeattimes;
	}
	
}
