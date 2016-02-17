package com.dinfo.crawl.enterprise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.dinfo.crawl.util.DateUtils;
import com.dinfo.hbase.HBaseAPP;
import com.dinfo.hbase.HbaseUtil;
/**
 * 解析pdf文件并入库
 * @author ljavaw
 */
public class EnterprisePDFAnalysis extends TimerTask{
	
	//map中的key标识（用于数据表中的数据）
	public String dataString = "data";
	//map中的key标识（用于索引表中的数据）
	public String intString = "index";
	
	HBaseAPP app = new HBaseAPP();
	
	/**
	 * 解析pdf文件并入库
	 */
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		//status  0:文件未下载1:文件已下载2:已解析入库(未同步)
		String status = "1";
		//一次查出的数据条数
		int pageSize = 100;
		
		while(true){
			List<EnterpriseReportBean> beanList = app.scanRecordEnterprise(EnterpriseTableInfo.tableName,
					EnterpriseTableInfo.familyNames, EnterpriseTableInfo.cellName2, status, pageSize);
			System.out.println("PDF解析，查出的数据数：》》》"+beanList.size());
			if(beanList != null && beanList.size() > 0){
				for(EnterpriseReportBean bean : beanList){
					if(bean.getLocalFilePath() != null && !("").equals(bean.getLocalFilePath())){
						Map<String, EnterpriseDynamicBean> map = new HashMap<String,EnterpriseDynamicBean>();
						if(bean.getFamilyName() != null && (EnterpriseTableInfo.familyName_annualReportInfo).equals(bean.getFamilyName())){
							//分析年报
							map = this.parseAnnualPdf(bean);
						}else if(bean.getFamilyName() != null && (EnterpriseTableInfo.familyName_noticeInfo).equals(bean.getFamilyName())){
							//分析公告
							map = this.parseNoticePdf(bean);
						}
						if(map != null && map.size() > 0){
							this.putData(map.get(dataString), EnterpriseTableInfo.tableName1);
							this.putData(map.get(intString), EnterpriseTableInfo.indexTableName1);
						}
					}
					bean.setStatus("2");//用于更新信息状态
				}
			}else{
				break;
			}
			try {
				app.updateRecord(EnterpriseTableInfo.tableName, beanList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析pdf文件1(解析的年报部分)
	 * @param pdfPath
	 */
	public Map<String, EnterpriseDynamicBean> parseAnnualPdf(EnterpriseReportBean bean){
		
		Map<String, EnterpriseDynamicBean> resultMap = new HashMap<String, EnterpriseDynamicBean>();
		EnterpriseDynamicBean resultBeanData = new EnterpriseDynamicBean();
		EnterpriseDynamicBean resultBeanIndex = new EnterpriseDynamicBean();
		PDDocument doc = null;
		try {
			doc = PDDocument.load(bean.getLocalFilePath());
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);
			
			//enterpriseAnnualReport(企业年报信息)
			Map<String, String> dataMap = new HashMap<String, String>();
			
			Map<String, String> tempMap = getMysqlData(bean.getStockCode());
			
			//stockCode	股票代码
			dataMap.put("stockCode", bean.getStockCode());
			//stockName	股票名称
			dataMap.put("stockName", tempMap.get("companyAbbreviation"));
			//companyName 公司名称
			dataMap.put("companyName", tempMap.get("companyName"));
			//releaseTime	发布时间
			dataMap.put("releaseTime", bean.getReleaseTime());
			//annualReportYear	年报年份
			if(bean.getTitle().contains("半年")){
				//isSemiReport	是否半年报
				dataMap.put("isSemiReport", "是");
				dataMap.put("annualReportYear", bean.getReleaseTime());
			}else{
				dataMap.put("isSemiReport", "否");
				String year = bean.getReleaseTime().substring(0, 4);
				if(year != null && !("").equals(year)){
					dataMap.put("annualReportYear", String.valueOf(Integer.valueOf(year)-1));
				}
			}
			//annualReportContent	年报内容
			dataMap.put("annualReportContent", text);
			//filePath	pdf文件路径
			dataMap.put("filePath", bean.getLocalFilePath());
			//getTime	采集时间
			dataMap.put("getTime", bean.getGetTime());
			
			String companyName = dataMap.get("companyName");
			
			System.out.println("stockCode>>>>>>>"+bean.getStockCode());
			System.out.println("companyName>>>>>>>"+companyName);
			System.out.println("getRemoteFilePath>>>>>>>"+bean.getRemoteFilePath());
			if(companyName == null || ("").equals(companyName) 
					|| bean.getRemoteFilePath() == null || ("").equals(bean.getRemoteFilePath())){
				return null;
			}
			resultBeanData.setFamilyName(EnterpriseTableInfo.familyNames1.get(2));//年报
			resultBeanData.setRowKey(HbaseUtil.md5(companyName)+"-"+HbaseUtil.md5(bean.getRemoteFilePath()));
			resultBeanData.setCells(dataMap);
			System.out.println("RowKey>>>>>"+HbaseUtil.md5(companyName)+"-"+HbaseUtil.md5(bean.getRemoteFilePath()));
			Map<String, String> indexMap = new HashMap<String, String>();
			indexMap.put(EnterpriseTableInfo.indexQualifier1, HbaseUtil.md5(companyName)+"-"+HbaseUtil.md5(bean.getRemoteFilePath()));
			resultBeanIndex.setFamilyName(EnterpriseTableInfo.indexFamily1);
			resultBeanIndex.setRowKey(DateUtils.SOLR_SDF.format(new Date()));
			resultBeanIndex.setCells(indexMap);
			
			resultMap.put("data", resultBeanData);
			resultMap.put("index", resultBeanIndex);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(doc != null){
					doc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultMap;
	}
	/**
	 * 解析pdf文件2(解析的公告部分)
	 * @param pdfPath
	 * @return 
	 */
	public Map<String, EnterpriseDynamicBean> parseNoticePdf(EnterpriseReportBean bean){
		
		Map<String, EnterpriseDynamicBean> resultMap = new HashMap<String, EnterpriseDynamicBean>();
		EnterpriseDynamicBean resultBeanData = new EnterpriseDynamicBean();
		EnterpriseDynamicBean resultBeanIndex = new EnterpriseDynamicBean();
		try {
			PDDocument doc = PDDocument.load(bean.getLocalFilePath());
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);

			//enterpriseAnnualReport(企业公告信息)
			Map<String, String> dataMap = new HashMap<String, String>();
			//stockCode	股票代码
			dataMap.put("stockCode", bean.getStockCode());
			//companyName 公司名称
			String companyName = getMysqlData(bean.getStockCode()).get("companyName");
			dataMap.put("companyName", companyName);
			//noticeDate 公告时间
			dataMap.put("releaseTime", bean.getReleaseTime());
			//noticeTitle 公告标题
			dataMap.put("noticeTitle", bean.getTitle());
			//noticeContent	信息内容
			dataMap.put("annualReportContent", text);
			//filePath	pdf文件路径
			dataMap.put("filePath", bean.getLocalFilePath());
			//getTime	采集时间
			dataMap.put("getTime", bean.getGetTime());
			
			resultBeanData.setFamilyName(EnterpriseTableInfo.familyNames1.get(1));//公告
			resultBeanData.setRowKey(HbaseUtil.md5(companyName)+"-"+HbaseUtil.md5(bean.getRemoteFilePath()));
			resultBeanData.setCells(dataMap);
			
			Map<String, String> indexMap = new HashMap<String, String>();
			indexMap.put(EnterpriseTableInfo.indexQualifier1, HbaseUtil.md5(companyName)+"-"+HbaseUtil.md5(bean.getRemoteFilePath()));
			resultBeanIndex.setFamilyName(EnterpriseTableInfo.indexFamily1);
			resultBeanIndex.setRowKey(DateUtils.SOLR_SDF.format(new Date()));
			resultBeanIndex.setCells(indexMap);
			
			resultMap.put("data", resultBeanData);
			resultMap.put("index", resultBeanIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 查询 enterpris_info_t表，获得相关信息
	 */
	public Map<String, String> getMysqlData(String stockCode){
		Map<String, String> map = new HashMap<String, String>();
		if(stockCode != null && !("").equals(stockCode)){
			EnterpriseHandleDatabase handle = new EnterpriseHandleDatabase();
			String querySql = "SELECT * FROM enterpris_info_t WHERE stockCode = "+stockCode;
			map = handle.getData3(querySql);
		}else{
			map.put("companyAbbreviation", "");
			map.put("companyName", "");
		}
		return map;
	}

	/**
	 * 插入"ENTERPRISE_DYNAMIC_INFO"数据表
	 */
	@SuppressWarnings("serial")
	public void putData(final EnterpriseDynamicBean bean, String tableName) {
		try {
			List<Map<String, String>> temp = new ArrayList<Map<String, String>>() {
				{
					add(bean.getCells());
				}
			};
			Set<String> rowKeys = new TreeSet<String>();
			rowKeys.add(bean.getRowKey());
			HBaseAPP.putRecord(tableName, rowKeys, bean.getFamilyName(),temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 插入"ENTERPRIS_REPORT_T"表中的"status"
	 */
	@SuppressWarnings({ "serial" })
	public void updateStatus(List<EnterpriseReportBean> list) {

		try {
			for (int i = 0; i < list.size(); i++) {
				EnterpriseReportBean bean = list.get(i);

				final Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put(EnterpriseTableInfo.cellName1, bean.getLocalFilePath());
				// status 0:文件未下载1:文件已下载 2:已解析入库(未同步)
				tempMap.put(EnterpriseTableInfo.cellName2, "2");
				List<Map<String, String>> temp = new ArrayList<Map<String, String>>() {
					{
						add(tempMap);
					}
				};

				Set<String> rowKeys = new TreeSet<String>();
				rowKeys.add(bean.getRowKey());
				HBaseAPP.putRecord(EnterpriseTableInfo.tableName, rowKeys, bean.getFamilyName(), temp);
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * 正则匹配日期
	 * @param str
	 * @return
	 */
	public String getRegularStr(String str){
		
		Pattern pattern = Pattern.compile(".*年.*月.*日.*");
		Matcher matcher = pattern.matcher(str);
		
		StringBuffer buffer = new StringBuffer();
		while(matcher.find()){             
		    buffer.append(matcher.group());       
		    buffer.append("/r/n");             
		System.out.println(buffer.toString());
		}
//			boolean b= matcher.matches();
//			//当条件满足时，将返回true，否则返回false
//			if(b){
//				return str;
//			}
		  return buffer.toString();
	}
}
