package com.dinfo.crawl.bean;

import java.util.List;
import java.util.Set;

public class UrlAndTemplateBean {
	
	private Integer id;
	
	private List<Integer> ids;
	
	private String template_content_source;
	
	private String template_content_bbs;
	
	private String template_content_comment;
	
	private String template_content_shop;
	
	private int page_num;
	
	private int page_num1;
	
	private String sourceUrl;
	
	private List<String> bbsUrlList;
	
	private List<String> commentUrlList;
	
	private List<String> shopCommentUrlList;
	
	private String sourceProvice;
	
	private String sourceCity;
	
	private Set<String> rowKeys;

	public String getTemplate_content_source() {
		return template_content_source;
	}

	public void setTemplate_content_source(String template_content_source) {
		this.template_content_source = template_content_source;
	}

	public String getTemplate_content_bbs() {
		return template_content_bbs;
	}

	public void setTemplate_content_bbs(String template_content_bbs) {
		this.template_content_bbs = template_content_bbs;
	}

	public String getTemplate_content_comment() {
		return template_content_comment;
	}

	public void setTemplate_content_comment(String template_content_comment) {
		this.template_content_comment = template_content_comment;
	}

	public int getPage_num() {
		return page_num;
	}

	public void setPage_num(int page_num) {
		this.page_num = page_num;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public List<String> getBbsUrlList() {
		return bbsUrlList;
	}

	public void setBbsUrlList(List<String> bbsUrlList) {
		this.bbsUrlList = bbsUrlList;
	}

	public List<String> getCommentUrlList() {
		return commentUrlList;
	}

	public void setCommentUrlList(List<String> commentUrlList) {
		this.commentUrlList = commentUrlList;
	}

	public String getTemplate_content_shop() {
		return template_content_shop;
	}

	public void setTemplate_content_shop(String template_content_shop) {
		this.template_content_shop = template_content_shop;
	}

	public List<String> getShopCommentUrlList() {
		return shopCommentUrlList;
	}

	public void setShopCommentUrlList(List<String> shopCommentUrlList) {
		this.shopCommentUrlList = shopCommentUrlList;
	}

	public String getSourceProvice() {
		return sourceProvice;
	}

	public void setSourceProvice(String sourceProvice) {
		this.sourceProvice = sourceProvice;
	}

	public String getSourceCity() {
		return sourceCity;
	}

	public void setSourceCity(String sourceCity) {
		this.sourceCity = sourceCity;
	}

	public int getPage_num1() {
		return page_num1;
	}

	public void setPage_num1(int page_num1) {
		this.page_num1 = page_num1;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<String> getRowKeys() {
		return rowKeys;
	}

	public void setRowKeys(Set<String> rowKeys) {
		this.rowKeys = rowKeys;
	}



	

}
