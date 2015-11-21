package com.dinfo.crawl.enterprise;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
	
	/**
	 * 解析pdf文件并入库
	 */
	@Override
	public void run() {
		while(true){
			EnterprisePDFAnalysis test = new EnterprisePDFAnalysis();
			List<EnterpriseReportBean> list = test.getLocalUrl();
			if(list != null && list.size() > 0){
				for(EnterpriseReportBean bean : list){
					if(bean.getLocalFilePath() != null && !("").equals(bean.getLocalFilePath())){
						Map<String, EnterpriseDynamicBean> map = new HashMap<String,EnterpriseDynamicBean>();
						
						if(bean.getFamilyName() != null && ("annualReportInfo").equals(bean.getFamilyName())){
							//分析年报
							map = test.parseAnnualPdf(bean);
						}else if(bean.getFamilyName() != null && ("noticeInfo").equals(bean.getFamilyName())){
							//分析公告
							map = test.parseNoticePdf(bean);
						}
						if(map != null && map.size() > 0){
							test.putData(map.get("data"), EnterpriseTableInfo.tableName1);
							test.putData(map.get("index"), EnterpriseTableInfo.indexTableName1);
						}
					}
				}
			}else{
				break;
			}
		}
	}
	
	/**
	 * 获得本地的pdf文件路径(默认一次只取100数据)
	 * 
	 * @return
	 */
	public List<EnterpriseReportBean> getLocalUrl() {
		HBaseAPP app = new HBaseAPP();
		List<EnterpriseReportBean> list = app.scanRecordEnterprise(EnterpriseTableInfo.tableName,
				EnterpriseTableInfo.familyNames, EnterpriseTableInfo.cellName2, "1");
		return list;
	}
	
	/**
	 * 解析pdf文件1(解析的年报部分)
	 * @param pdfPath
	 * @return 
	 */
	public Map<String, EnterpriseDynamicBean> parseAnnualPdf(EnterpriseReportBean bean){
		
		Map<String, EnterpriseDynamicBean> resultMap = new HashMap<String, EnterpriseDynamicBean>();
		EnterpriseDynamicBean resultBeanData = new EnterpriseDynamicBean();
		EnterpriseDynamicBean resultBeanIndex = new EnterpriseDynamicBean();
		try {
			PDDocument doc = PDDocument.load(bean.getLocalFilePath());
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);
			
			//enterpriseAnnualReport(企业年报信息)
			Map<String, String> dataMap = new HashMap<String, String>();
			//stockCode	股票代码
			dataMap.put("stockCode", bean.getStockCode());
			//stockName	股票名称
			dataMap.put("stockName", getMysqlData(bean.getStockCode()).get("companyAbbreviation"));
			//companyName 公司名称
			String companyName = getMysqlData(bean.getStockCode()).get("companyName");
			dataMap.put("companyName", companyName);
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
				HBaseAPP.putRecord(EnterpriseTableInfo.tableName, rowKeys, bean.getFamilyName(),
						temp);
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
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		try {
			String dir = "e:/pdf/";
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			//上市交易所 0:上交所 1：深交所
			String url = "http://static.sse.com.cn/disclosure/listedinfo/announcement/c/2015-09-07/600983_20150907_1.pdf";
			URL u = new URL(url);
				
			InputStream i = u.openStream();
			byte[] b = new byte[1024*1024];
			int len;
			String fileName = url.substring(url.lastIndexOf("/")+1);
			OutputStream bos;

			bos = new FileOutputStream(new File(dir + fileName));
			while ((len = i.read(b)) != -1) {
					bos.write(b, 0, len);
				}
			bos.flush();
			bos.close();
			i.close();
			System.out.println("下载完成,本地路径为："+(dir + fileName));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*Pattern pattern = Pattern.compile(".*年.*月.*日.*");
		Matcher matcher = pattern.matcher("二○一五年三月十日");
		StringBuffer buffer = new StringBuffer();
		while(matcher.find()){             
		    buffer.append(matcher.group());       
		System.out.println(buffer.toString());
		}*/
	}
}
