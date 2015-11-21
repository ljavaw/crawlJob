package com.dinfo.crawl.test;

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
import com.dinfo.crawl.bean.Page;
import com.dinfo.crawl.bean.Parameter;
import com.dinfo.crawl.bean.Url;
import com.dinfo.crawl.bean.UrlAndTemplateBean;
import com.dinfo.crawl.conf.JobConfig;
import com.dinfo.crawl.conf.ParseXML;
import com.dinfo.crawl.util.XMLUtil;

/**
 * 查询原始信息表和模板表
 * @author ljavaw
 *
 */
public class Task1 extends TimerTask {

	//private static Logger logger = Util.sample("com.dinfo.crawl.test.Task1");
	private static final Log logger = LogFactory.getLog(Task1.class);
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

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			//addInfo("模板名称2", "D:/workspace_git/crawl/搜房网详情.xml", 2);
			ParseXML parseXml = new ParseXML();// 调用的外面的解析方法
			XMLUtil xmlUtil = new XMLUtil();
			TaskCrawl taskCrawl = new TaskCrawl();// 生成任务的方法
			// 查询原始信息表和模板表
			String querySqlAll = "SELECT s.*,t.template_content FROM source_info_t s , template_info_t t WHERE s.template_id = t.id AND s.collect_state=0 AND t.crawl_type=0 AND s.url IS NOT null";
			String querySqlIncrease = "SELECT s.*,t.template_content FROM source_info_t s , template_info_t t WHERE s.template_id = t.id AND s.collect_state=0 AND t.crawl_type=1 AND s.url IS NOT null";
			List<List<UrlAndTemplateBean>> list = new ArrayList<List<UrlAndTemplateBean>>();
			list.add(handle.getData1(querySqlAll));
			list.add(handle.getData1(querySqlIncrease));
			
			List<Integer> idList = new ArrayList<Integer>();
			for(int x = 0; x < list.size(); x++){
				List<UrlAndTemplateBean> beans = list.get(x);
				//当x=0时是全量采集数据，只执行一次，是当前时间向后推迟一小时开始执行，getCrawlDate（）获得执行时间的方法
				String[] t1Array = (x == 0 ? Util.getCrawlDate().split(",") : MyTest.CRAWL_PROPERTIES.get("templateXmlTimeTask1").split(","));
				String year = t1Array[0];
				String mouth = t1Array[1];
				String day = t1Array[2];
				String week = t1Array[3];
				String hourCron = t1Array[4];
				String minuteCron = t1Array[5];
				String secondCron = t1Array[6];
				int z = 1;
				if(beans != null && beans.size() > 0){
					for (UrlAndTemplateBean result : beans) {
						if (result.getPage_num() != 0) {
							
							Integer id = result.getId();
							int page_num = result.getPage_num();
							String province = result.getSourceProvice();
							String city = result.getSourceCity();
							String template_content = result.getTemplate_content_source();
							String dataUrl = result.getSourceUrl();
							try {
								Crawl crawl = (Crawl) xmlUtil.converyToJavaBean(template_content, Class.forName("com.dinfo.crawl.bean.Crawl"));
								
								Job job = crawl.getJobList().get(0);
								
								String jobName = job.getJobName();
								
								List<Url> urlList = job.getPageList().get(0).getUrls()
										.getUrlList();
								
								String baseUrl = dataUrl.substring(0,
										dataUrl.length() - 3);// 根据具体形式进行修改
								
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
								int temp = 1;
								for (int i = 1; i <= jobNum; i++) {
									Crawl newCrawl = (Crawl) crawl.deepClone();
									
									Job tempJob = (Job) job.deepClone();
									tempJob.setJobCron(Util.getCorn1(secondCron,
											minuteCron, hourCron,day, mouth, week, year, z));
									tempJob.setJobName(jobName + UUID.randomUUID());
									for (int j = 1; j <= (i == jobNum ? (pageNum
											% URLNUM == 0 ? URLNUM : pageNum % URLNUM)
											: URLNUM); j++) {
										Url tempUrl = new Url();
										tempUrl.setUrlStr(baseUrl + (temp++));
										tempJob.getPageList().get(0).getUrls()
										.getUrlList().add(tempUrl);
									}
									List<Page> pagesTemp = tempJob.getPageList();
									List<Parameter> parametersTemp = pagesTemp
											.get(pagesTemp.size() - 1).getParse()
											.getParaList();
									
									Parameter provinceP = parametersTemp
											.get(parametersTemp.size() - 3);
									provinceP.setParaExpression(province);// 把source表中查出的省set到生成的config中
									Parameter cityP = parametersTemp.get(parametersTemp
											.size() - 2);
									cityP.setParaExpression(city);// 把source表中查出的市set到生成的config中
									
									newCrawl.getJobList().add(tempJob);// 添加job对象
									String xml = xmlUtil.convertToXml(newCrawl, "UTF-8");
									// System.out.println("生成的新xml是："+xml+"\n==============end");
									// util.stringToXml(xml,"D:/abc.xml");//输出xml文件
									
									Document doc = DocumentHelper.parseText(xml);
									Map<String, JobConfig> resultMap = parseXml.parseXML(doc);// 生成一个job
									addConfigMap.put(xml, resultMap);
									taskCrawl.crawlStart(resultMap,1);// 生成任务 1:社区 2：企业
									logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task1)生成任务！");
									z++;
								}
								handle.addConfigInfo(addConfigMap);// 添加到config_info_t数据库
								logger.info(">>>>>>>>>>>>>>>>>>>>>>>(Task1)添加到config_info_t数据库完成！");
								idList.add(id);
								
							} catch (Exception e) {
								logger.error(e);
								e.printStackTrace();
							}
						} else {
							logger.info("》》》》》》生成任务1   没有需要采集的url");
						}
					}
				}else{
					logger.info("》》》》》》生成任务1   没有需要采集的url");
				}
			}

			// 执行完任务之后，把采集状态置为1
			handle.updateField("UPDATE source_info_t SET collect_state=1 WHERE id = ?", idList);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			HandleDatabase.closeDataSource(rs1, rs2, pstmt1, pstmt2, conn);
		}

	}

}
