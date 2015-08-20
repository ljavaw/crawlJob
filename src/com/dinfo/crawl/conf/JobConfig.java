package com.dinfo.crawl.conf;

import java.io.Serializable;
import java.util.List;

public class JobConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name; // jobName
	
	private String cronExp;  // job 执行时间表达式
	
	private List<PageConfig> pageConList;  // 对应的pageConfig
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCronExp() {
		return cronExp;
	}

	public void setCronExp(String cronExp) {
		this.cronExp = cronExp;
	}

	public List<PageConfig> getPageConList() {
		return pageConList;
	}

	public void setPageConList(List<PageConfig> pageConList) {
		this.pageConList = pageConList;
	}
	
	

}
