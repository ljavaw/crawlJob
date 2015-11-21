package com.dinfo.crawl.enterprise;

import java.util.Map;

/**
 * 向(企业动态信息表)和(企业动态信息索引表)中插入数据时使用的bean
 * @author ljavaw
 */
public class EnterpriseDynamicBean {
	
	private String rowKey;
	
	private String familyName;
	
	private Map<String, String> cells;
	

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public Map<String, String> getCells() {
		return cells;
	}

	public void setCells(Map<String, String> cells) {
		this.cells = cells;
	}

}
