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
	
	private String ssavetype;  // 源码保存类型
	 
	private String spath; // 源码保存路径
	
	private String jobType; // 采集任务 或者  解析任务 
	
	private List<PageConfig> pageConList;  // 对应的pageConfig
	
	
	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getSsavetype() {
		return ssavetype;
	}

	public void setSsavetype(String ssavetype) {
		this.ssavetype = ssavetype;
	}

	public String getSpath() {
		return spath;
	}

	public void setSpath(String spath) {
		this.spath = spath;
	}

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
