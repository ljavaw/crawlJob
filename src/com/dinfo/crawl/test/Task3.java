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
 * 查询商户url和模板
 * 
 * @author ljavaw
 * 
 */
public class Task3 extends TimerTask {

	private static final Log logger = LogFactory.getLog(Task3.class);
	private static Integer URLNUM = 100; // 每个job中含有的url个数
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
			// 查询原始信息表和模板表( template_type
			// 0:社区1:论坛2:点评3:商户;platform_type平台类型:0:搜房1:安居客2:大众点评3:特惠商户4:企业)
			// (注：所有商户都来自于大众点评的平台)
			String querySqlAll = "SELECT t.template_content FROM template_info_t t WHERE t.platform_type=2 AND t.template_type = 3 AND t.crawl_type = 0 ";
			String querySqlIncrease = "SELECT t.template_content FROM template_info_t t WHERE t.platform_type=2 AND t.template_type = 3 AND t.crawl_type = 1";
			
			List<UrlAndTemplateBean> list = new ArrayList<UrlAndTemplateBean>();
			list.add(handle.getData33("SHOP_INFO_T", querySqlAll));
			list.add(handle.getData33("SHOP_INFO_T", querySqlIncrease));
			for(int i = 0; i < list.size(); i++){
				UrlAndTemplateBean result = list.get(i);
				String template_content = result.getTemplate_content_shop();
				Set<String> rowKeys = result.getRowKeys();
				List<String> urls = result.getShopCommentUrlList();
				if (urls != null && urls.size() > 0 && rowKeys != null && rowKeys.size() > 0
						&& template_content != null && !("").equals(template_content)) {
					int page_num = urls.size();
					runTask(template_content, urls, page_num, i);// Url
					// 两种任务都执行完之后，把采集状态置为1
					if(i == 1){
						handle.updateField("SHOP_INFO_INDEX","shopInfoIndex", "dispost_status", rowKeys);
					}
				} else {
					logger.info("》》》》》》生成任务3   没有需要采集的url");
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			HandleDatabase.closeDataSource(rs1, rs2, pstmt1, pstmt2, conn);
		}
	}

	@SuppressWarnings({ "static-access" })
	public void runTask(String template_content, List<String> urls, int page_num, int crawlState) {

		ParseXML parseXml = new ParseXML();// 调用的外面的解析方法
		XMLUtil xmlUtil = new XMLUtil();
		TaskCrawl taskCrawl = new TaskCrawl();// 生成任务的方法

		try {
			Crawl crawl = (Crawl) xmlUtil.converyToJavaBean(template_content,
					Class.forName("com.dinfo.crawl.bean.Crawl"));

			Job job = crawl.getJobList().get(0);
			String jobName = job.getJobName();

			String[] t3Array = crawlState == 0 ? Util.getCrawlDate().split(",") : MyTest.CRAWL_PROPERTIES.get("templateXmlTimeTask3").split(",");
			String year = t3Array[0];
			String mouth = t3Array[1];
			String day = t3Array[2];
			String week = t3Array[3];
			String hourCron = t3Array[4];
			String minuteCron = t3Array[5];
			String secondCron = t3Array[6];

			List<Url> urlList = job.getPageList().get(0).getUrls().getUrlList();

			Integer jobNum;// 每个job标签中最多有100url
			Integer pageNum = page_num;
			if (pageNum % URLNUM == 0) {
				jobNum = pageNum / URLNUM;
			} else {
				jobNum = pageNum / URLNUM + 1;
			}

			crawl.getJobList().clear();// 清空job对象
			urlList.clear();// 清空url对象

			Map<String, Map<String, JobConfig>> addConfigMap = new HashMap<String, Map<String, JobConfig>>();
			for (int i = 1; i <= jobNum; i++) {
				Crawl newCrawl = (Crawl) crawl.deepClone();
				Job tempJob = (Job) job.deepClone();
				tempJob.setJobCron(Util.getCorn1(secondCron, minuteCron, hourCron,day, mouth, week, year, i));
				tempJob.setJobName(jobName + UUID.randomUUID());
				for (int j = 1; j <= (i == jobNum ? (pageNum % URLNUM == 0 ? URLNUM
						: pageNum % URLNUM)
						: URLNUM); j++) {
					Url tempUrl = new Url();
					tempUrl.setUrlStr(urls.get(i * j - 1));
					tempJob.getPageList().get(0).getUrls().getUrlList()
							.add(tempUrl);
				}
				newCrawl.getJobList().add(tempJob);// 添加job对象
				String xml = xmlUtil.convertToXml(newCrawl, "UTF-8");
				Document doc = DocumentHelper.parseText(xml);
				Map<String, JobConfig> resultMap = parseXml.parseXML(doc);// 只生成了一个job
				addConfigMap.put(xml, resultMap);

				taskCrawl.crawlStart(resultMap);// 生成任务(一个job)
				logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task3)生成任务！");
			}
			handle.addConfigInfo(addConfigMap);// 添加到数据库
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task3)添加config到数据库！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
