package com.dinfo.crawl.enterprise;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseTableInfo {

	// 上市公司报告索引表   表名
	public static final String indexTableName = "ENTERPRIS_NOTICE_INDEX";
	// 上市公司报告索引表   列族名   
	public static final String indexFamily = "reportIndex";
	// 上市公司报告索引表    列名
	public static final String indexQualifier = "reportKeys";
	
	// 上市公司报告  数据表  表名
	public static final String tableName = "ENTERPRIS_REPORT_T";
	// 上市公司报告  数据表  列族名
	@SuppressWarnings("serial")
	public static final List<String> familyNames = new ArrayList<String>() {
		{
			add("noticeInfo");
			add("annualReportInfo");
		}
	};
	// 上市公司报告  数据表  列名
	@SuppressWarnings("serial")
	public static final List<String> cellNames = new ArrayList<String>() {
		{
			add("noticeTitle");
			add("noticeDate");
			add("stockCode");
			add("remoteFilePath");
			add("localFilePath");
			add("getTime");
			add("status");
			add("annualReportTitle");
			add("reportDate");
		}
	};
	// 上市公司报告  数据表 指定的列名
	public static final String cellName = "remoteFilePath";
	// 上市公司报告  数据表 指定的列名
	public static final String cellName1 = "localFilePath";
	// 上市公司报告  数据表 指定的列名
	public static final String cellName2 = "status";
	
	
	
	//企业动态信息索引表   表名
	public static final String indexTableName1 = "ENTERPRISE_DYNAMIC_INDEX";
	//企业动态信息索引表  列族名   
	public static final String indexFamily1 = "enterpriseDynamicIndex";
	//企业动态信息索引表    列名
	public static final String indexQualifier1 = "enterpriseDynamicKeys";
	
	//企业动态信息表  数据表  表名
	public static final String tableName1 = "ENTERPRISE_DYNAMIC_INFO";
	//企业动态信息表  数据表  列族名
	@SuppressWarnings("serial")
	public static final List<String> familyNames1 = new ArrayList<String>() {
		{
			add("enterpriseDynamicInfo");
			add("enterpriseNoticeInfo");
			add("enterpriseAnnualReport");
		}
	};
	//企业动态信息表  数据表  列名
	@SuppressWarnings("serial")
	public static final List<String> cellNames1 = new ArrayList<String>() {
		{
			add("infoSources");//	信息来源(和讯、腾讯、百度搜索、新浪)
			add("releaseTime");//	发布时间
			add("infoTitle");//	信息标题
			add("infoContent");//	信息内容
			add("getTime");//	采集时间
			add("stockCode");//	股票代码
			add("companyName");//	公司名称
			add("noticeDate");//	公告时间
			add("noticeTitle");//	公告标题
			add("noticeContent");//	信息内容
			add("filePath");//	pdf文件路径
			add("getTime");//	采集时间
			add("stockCode");//	股票代码
			add("stockName");//	股票名称
			add("companyName");//	公司名称
			add("annualReportYear");//	年报年份
			add("releaseTime");//	发布时间
			add("isSemiReport");//	是否半年报
			add("annualReportContent");//	年报内容
			add("filePath");//	pdf文件路径
			add("getTime");//	采集时间
		}
	};
	
}
