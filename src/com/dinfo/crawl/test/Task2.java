package com.dinfo.crawl.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.dinfo.crawl.util.XMLUtil;
/**
 * bbsUrl+template
 * 查询社区信息表和模板表(template_type:  0:社区1:论坛2:点评3:商户)
 * @author ljavaw
 *
 */
public class Task2 extends TimerTask {

	private static final Log logger = LogFactory.getLog(Task2.class);
	private static Integer URLNUM = 100; //每个job中含有的url个数
	
	static Connection conn;
	PreparedStatement pstmt1;
	PreparedStatement pstmt2;
	ResultSet rs1;
	ResultSet rs2;
	
	static{
		conn = HandleDatabase.getConnection();
	}
	HandleDatabase handle = new HandleDatabase(conn, pstmt1, pstmt2, rs1, rs2);
	
	@Override
	public void run() {
		
		try {
			String querySqlAll2 = "SELECT * FROM template_info_t WHERE crawl_type = 0 AND id = 4 OR id = 5";
			String querySqlIncrease = "SELECT * FROM template_info_t WHERE crawl_type = 1 AND id = 4 OR id = 5";
			
			List<UrlAndTemplateBean> list = new ArrayList<UrlAndTemplateBean>();
			list.add(handle.getData22("COMMUNITY_INFO_T", querySqlAll2));
			list.add(handle.getData22("COMMUNITY_INFO_T", querySqlIncrease));
			Set<String > rowKeys = null;
			for(int i = 0; i < list.size(); i++){
				UrlAndTemplateBean result = list.get(i);
				rowKeys = result.getRowKeys();
				String template_content_bbs = result.getTemplate_content_bbs();
				List<String> bbsUrls = result.getBbsUrlList();
				String template_content_comment = result.getTemplate_content_comment();
				List<String> commentUrls = result.getCommentUrlList();
				
				if(bbsUrls != null && bbsUrls.size() > 0 && 
						template_content_bbs != null && !("").equals(template_content_bbs)){
					runTask(template_content_bbs, bbsUrls, bbsUrls.size(), i);//bbsUrl
				}
				if(commentUrls != null && commentUrls.size() > 0 &&
						template_content_comment != null && !("").equals(template_content_comment)){
					runTask(template_content_comment, commentUrls, commentUrls.size(), i);//commentUrl
				}
			}
			if(rowKeys != null && rowKeys.size() > 0){
				//执行完任务之后，把采集状态置为1
				handle.updateField("COMMUNITY_KEYS_INDEX","communityIndex","dispost_status",rowKeys);
				logger.info("》》》》》》生成任务2后，把采集状态置为1");
			}else{
				logger.info("》》》》》》生成任务2   没有需要采集的url");
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}finally{
			HandleDatabase.closeDataSource(rs1, rs2, pstmt1, pstmt2, conn);
		}
	}
	
	@SuppressWarnings({ "static-access"})
	public void runTask(String template_content, List<String> urls, int page_num, int crawlState){
		
		ParseXML parseXml = new ParseXML();//调用的外面的解析方法
		XMLUtil xmlUtil = new XMLUtil();
		TaskCrawl taskCrawl = new TaskCrawl();//生成任务的方法
		
		try {
			Crawl crawl = (Crawl) xmlUtil.converyToJavaBean(template_content, Class.forName("com.dinfo.crawl.bean.Crawl"));
			
			Job job = crawl.getJobList().get(0);
			String jobName = job.getJobName();
			//crawlState为0时是全部采集，为1时是增量采集
	    	String[] t2Array = crawlState == 0 ? Util.getCrawlDate().split(",") : MyTest.CRAWL_PROPERTIES.get("templateXmlTimeTask2").split(",");
			String year = t2Array[0];
			String mouth = t2Array[1];
			String day = t2Array[2];
			String week = t2Array[3];
			String hourCron = t2Array[4];
			String minuteCron = t2Array[5];
			String secondCron = t2Array[6];
			
			
			List<Url> urlList = job.getPageList().get(0).getUrls().getUrlList();
			
			Integer jobNum;//每个job标签中最多有100url
			Integer pageNum = page_num;
			if( pageNum % URLNUM == 0){
				jobNum = pageNum / URLNUM;
			}else {
				jobNum = pageNum / URLNUM + 1;
			}
			
			crawl.getJobList().clear();//清空job对象
			urlList.clear();//清空url对象
			
			Map<String, Map<String, JobConfig>> addConfigMap = new HashMap<String, Map<String, JobConfig>>();
			for (int i = 1; i <= jobNum; i++) {
				Crawl newCrawl = (Crawl) crawl.deepClone();
				
				Job tempJob = (Job) job.deepClone();
				tempJob.setJobCron(Util.getCorn1(secondCron,
						minuteCron, hourCron,day, mouth, week, year, i));
				tempJob.setJobName(jobName+UUID.randomUUID());
				for (int j = 1; j <= (i==jobNum?(pageNum % URLNUM == 0 ? URLNUM : pageNum % URLNUM):URLNUM); j++){
					Url tempUrl = new Url();
					tempUrl.setUrlStr(urls.get(i*j-1));
					tempJob.getPageList().get(0).getUrls().getUrlList().add(tempUrl);
				}
				newCrawl.getJobList().add(tempJob);//添加job对象
				
				String xml = xmlUtil.convertToXml(newCrawl, "UTF-8");
				Document doc = DocumentHelper.parseText(xml);
				Map<String, JobConfig> resultMap = parseXml.parseXML(doc);//只生成了一个job
				addConfigMap.put(xml, resultMap);
				
				taskCrawl.crawlStart(resultMap);//生成任务(一个job)
				logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task2)生成任务！");
			}
			
			handle.addConfigInfo(addConfigMap);//添加到数据库
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task2)添加config到数据库！");
			
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} 
		
	}

}

