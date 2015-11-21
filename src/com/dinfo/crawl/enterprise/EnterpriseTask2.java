package com.dinfo.crawl.enterprise;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.dinfo.crawl.TaskCrawl;
import com.dinfo.crawl.bean.Crawl;
import com.dinfo.crawl.bean.Job;
import com.dinfo.crawl.bean.Url;
import com.dinfo.crawl.bean.UrlAndTemplateBean;
import com.dinfo.crawl.conf.JobConfig;
import com.dinfo.crawl.conf.ParseXML;
import com.dinfo.crawl.test.MyTest;
import com.dinfo.crawl.test.Util;
import com.dinfo.crawl.util.XMLUtil;

/**
 * 查询原始信息表和模板表（id为 非 7,8,9的基础信息）
 * @author ljavaw
 *
 */
public class EnterpriseTask2 extends TimerTask {

	private static final Log logger = LogFactory.getLog(EnterpriseTask2.class);
	Connection conn = EnterpriseHandleDatabase.getConnection();
	PreparedStatement pstmt1;
	ResultSet rs1;
	EnterpriseHandleDatabase handle = new EnterpriseHandleDatabase(conn, pstmt1,rs1);
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			//addInfo("模板名称2", "D:/workspace_git/crawl/搜房网详情.xml", 2);
			ParseXML parseXml = new ParseXML();// 调用的外面的解析方法
			XMLUtil xmlUtil = new XMLUtil();
			TaskCrawl taskCrawl = new TaskCrawl();// 生成任务的方法
			// 查询原始信息表和模板表
			String querySql = "SELECT * FROM template_info_t WHERE template_type=4 and id not in(7,8,9)";
			List<List<UrlAndTemplateBean>> list = new ArrayList<List<UrlAndTemplateBean>>();
			list.add(handle.getData1(querySql));
			
			for(int x = 0; x < list.size(); x++){
				List<UrlAndTemplateBean> beans = list.get(x);
				String[] t1Array = MyTest.CRAWL_PROPERTIES.get("templateXmlTimeEnterpriseTask2").split(",");
				String year = t1Array[0];
				String mouth = t1Array[1];
				String day = t1Array[2];
				String week = t1Array[3];
				String hourCron = t1Array[4];
				String minuteCron = t1Array[5];
				String secondCron = t1Array[6];
				if(beans != null && beans.size() > 0){
					for (UrlAndTemplateBean result : beans) {
						String template_content = result.getTemplate_content_source();
						Crawl crawl = (Crawl) xmlUtil.converyToJavaBean(template_content, Class.forName("com.dinfo.crawl.bean.Crawl"));
						
						Job tempJob = crawl.getJobList().get(0);
						
						String jobName = tempJob.getJobName();
						tempJob.setJobCron(Util.getCorn1(secondCron,minuteCron, hourCron, day, mouth, week, year, 1));
						tempJob.setJobName(jobName + UUID.randomUUID());
						
						//当id为2(上市公司列表(深交所主板))或者3(上市公司列表(深交所创业板))的模板时，进行url拼接
						String tempTemplateName = result.getTemplateName().substring(result.getTemplateName().lastIndexOf("&")+1);
						//以下数字的含义参看数据库模板表
						if(("2").equals(tempTemplateName) || ("3").equals(tempTemplateName)){
							tempJob = getResultUrl1(tempJob);
							crawl.getJobList().clear();// 清空job对象
							crawl.getJobList().add(tempJob);// 添加新job对象
							
						}else if(("10").equals(tempTemplateName)){
							tempJob = getResultUrl2(tempJob);
							crawl.getJobList().clear();// 清空job对象
							crawl.getJobList().add(tempJob);// 添加新job对象
							
						}else if(("11").equals(tempTemplateName)){
							tempJob = getResultUrl3(tempJob);
							crawl.getJobList().clear();// 清空job对象
							crawl.getJobList().add(tempJob);// 添加新job对象
							
						}else if(("12").equals(tempTemplateName) || ("15").equals(tempTemplateName)){
							tempJob = getResultUrl4(tempJob);
							crawl.getJobList().clear();// 清空job对象
							crawl.getJobList().add(tempJob);// 添加新job对象
						}else if(("5").equals(tempTemplateName) || ("6").equals(tempTemplateName)){
							tempJob = getResultUrl5(tempJob);
							crawl.getJobList().clear();// 清空job对象
							crawl.getJobList().add(tempJob);// 添加新job对象
						}
						String xml = xmlUtil.convertToXml(crawl, "UTF-8");
						// System.out.println("生成的新xml是："+xml+"\n==============end");
						// util.stringToXml(xml,"D:/abc.xml");//输出xml文件
						
						Document doc = DocumentHelper.parseText(xml);
						Map<String, JobConfig> resultMap = parseXml.parseXML(doc);// 生成一个job
						Map<String, Map<String, JobConfig>> addConfigMap = new HashMap<String, Map<String, JobConfig>>();
						addConfigMap.put(xml, resultMap);
						taskCrawl.crawlStart(resultMap, 2);// 生成任务   1:社区 2：企业
						logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task2)生成任务！");
						handle.addConfigInfo(addConfigMap);// 添加到config_enterprise_info_t数据库
						logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task2)添加到config_enterprise_info_t数据库完成！");
					}
				}else{
					logger.info("》》》》》》生成任务2   没有需要采集的url");
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			EnterpriseHandleDatabase.closeDataSource(rs1, null, pstmt1, null, conn);
		}

	}
	/**
	 * 拼接翻页的url
	 * @param tempJob
	 * @param baseUrl
	 */
	public Job getResultUrl1(Job tempJob){
		
		String baseUrl = tempJob.getPageList().get(0).getUrls().getUrlList().get(0).getUrlStr();
		tempJob.getPageList().get(0).getUrls().getUrlList().clear();//清除urls
		
		//根据实际情况进行修改
		int pageCount = Integer.parseInt(baseUrl.split("tab1PAGECOUNT=")[1].substring(0, 2));
		String baseUrl0 = baseUrl.split("tab1PAGENUM=")[0];
		String baseUrl1 = baseUrl.split("tab1PAGENUM=")[1].substring(1);
		for(int i=0; i<pageCount; i++){
			Url tempUrl = new Url();
			tempUrl.setUrlStr(baseUrl0 + "tab1PAGENUM=" +(i+1) + baseUrl1);
			tempJob.getPageList().get(0).getUrls().getUrlList().add(tempUrl);
		}
		return tempJob;
	}
	/**
	 * 需要修改"公司名称"，"公司代码" 的url
	 * @param tempJob
	 * @param baseUrl
	 */
	public Job getResultUrl2(Job tempJob){
		
		try {
			Job job = (Job) tempJob.deepClone();
			List<Url> baseUrls = job.getPageList().get(0).getUrls().getUrlList();
			
			tempJob.getPageList().get(0).getUrls().getUrlList().clear();//清除urls
			String querySql = "SELECT * FROM enterpris_info_t";
			List<UrlAndTemplateBean> beanList = handle.getData2(querySql);
			//根据实际情况进行修改
			for(int i=0; i<beanList.size(); i++){
				UrlAndTemplateBean bean = beanList.get(i);
				for(int j=0; j<baseUrls.size(); j++){
					Url tempUrl = new Url();
					tempUrl.setCarrydata("companyname::"+bean.getCompanyName());
					tempUrl.setUrlStr(baseUrls.get(j).getUrlStr().replace("公司代码", bean.getStockCode()));
					tempJob.getPageList().get(0).getUrls().getUrlList().add(tempUrl);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempJob;
	}
	/**
	 * 需要修改"公司代码"的url
	 * @param tempJob
	 * @param baseUrl
	 */
	public Job getResultUrl3(Job tempJob){
		
		String baseUrl = tempJob.getPageList().get(0).getUrls().getUrlList().get(0).getUrlStr();
		tempJob.getPageList().get(0).getUrls().getUrlList().clear();//清除urls
		
		String querySql = "SELECT * FROM enterpris_info_t";
		List<UrlAndTemplateBean> beanList = handle.getData2(querySql);
		//根据实际情况进行修改
		for(int i=0; i<beanList.size(); i++){
			UrlAndTemplateBean bean = beanList.get(i);
			Url tempUrl = new Url();
			tempUrl.setUrlStr(baseUrl.replace("公司代码", bean.getStockCode()));
			tempJob.getPageList().get(0).getUrls().getUrlList().add(tempUrl);
		}
		return tempJob;
	}
	/**
	 * 需要修改"公司名称"和"对公司名称加密" 的url
	 * @param tempJob
	 * @param baseUrl
	 */
	public Job getResultUrl4(Job tempJob){
		try {
			String baseUrl = tempJob.getPageList().get(0).getUrls().getUrlList().get(0).getUrlStr();
			tempJob.getPageList().get(0).getUrls().getUrlList().clear();//清除urls
			
			String querySql = "SELECT * FROM enterpris_info_t";
			List<UrlAndTemplateBean> beanList = handle.getData2(querySql);
			//根据实际情况进行修改
			for (int i = 0; i < beanList.size(); i++) {
				UrlAndTemplateBean bean = beanList.get(i);
				Url tempUrl = new Url();
				tempUrl.setCarrydata("enterprise_name::"+bean.getCompanyName());
				tempUrl.setUrlStr(baseUrl
						.replace("加密后的公司名称",URLEncoder.encode(bean.getCompanyName(), "utf-8")));
				tempJob.getPageList().get(0).getUrls().getUrlList().add(tempUrl);
			}
		} catch (Exception e) {
		}
		return tempJob;
	}
	/**
	 * 需要修改"开始时间"和"结束时间"的url 格式为"2015-05-27"
	 * @param tempJob
	 * @param baseUrl
	 */
	public Job getResultUrl5(Job tempJob){
		try {
			String baseUrl = tempJob.getPageList().get(0).getUrls().getUrlList().get(0).getUrlStr();
			tempJob.getPageList().get(0).getUrls().getUrlList().clear();//清除urls
			
			//三个月的毫秒数
			long interval = 7776000000L;
			//根据实际情况进行修改
			Url tempUrl = new Url();
			tempUrl.setUrlStr(baseUrl.replace("结束时间",Util.getCrawlDate2(0))
					.replace("开始时间",Util.getCrawlDate2(interval)));
			tempJob.getPageList().get(0).getUrls().getUrlList().add(tempUrl);
		} catch (Exception e) {
		}
		return tempJob;
	}
	
}
