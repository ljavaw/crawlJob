package com.dinfo.crawl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class URLBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int count = 0;
	
	private String url;  // 请求url
	
	private List<String> list;  // url拼装字段值  按MessageFormat 中的不确定字段进行填充
	
	private Map<String,String> paraMap;  //请求map
	
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParaMap() {
		return paraMap;
	}

	public void setParaMap(Map<String, String> paraMap) {
		this.paraMap = paraMap;
	}

}
