package com.dinfo.crawl.enterprise;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.dinfo.hbase.HBaseAPP;

public class EnterprisePDFDownload {
	
	//上市公司报告索引表信息
	public static final String indexTableName = "ENTERPRIS_NOTICE_INDEX";
	public static final String indexFamily = "reportIndex";
	public static final String indexQualifierUrl = "reportKeys";
	//上市公司报告数据表信息
	public static final String tableName = "ENTERPRIS_REPORT_T";
	@SuppressWarnings("serial")
	public static final List<String> familyNames = new ArrayList<String>(){{add("noticeInfo");add("annualReportInfo");}};
	@SuppressWarnings("serial")
	public static final List<String> cellNames = new ArrayList<String>(){{add("noticeTitle");add("noticeDate");
	add("stockCode");add("remoteFilePath");add("localFilePath");add("getTime");add("status");
	add("annualReportTitle");add("reportDate");}};
	public static final String cellName = "localFilePath";
	
	/**
	 * 获得下载的url
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<EnterpriseReportBean> getData(){
		try {
			HBaseAPP app = new HBaseAPP();
			Map<String, Set<?>> resultMap = app.getIndexInfo(indexTableName, indexFamily, null, indexQualifierUrl);
			Set<String> cellSet = (Set<String>) resultMap.get("cells");
			List<EnterpriseReportBean> list = app.getRecordEnterprise(tableName, cellSet, familyNames, cellNames);
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 下载pdf文件
	 */
	public List<EnterpriseReportBean> downloadPDF(List<EnterpriseReportBean> list){
		List<EnterpriseReportBean> resultList = new ArrayList<EnterpriseReportBean>();
		try {
				for(EnterpriseReportBean bean : list){
					String url = bean.getRemoteFilePath();
						
					String dir = "d:/pdf/";
					File file = new File(dir);
					if (!file.exists()) {
						file.mkdirs();
					}
					
					// 遍历pdf地址的list，逐个下载
					URL u = new URL(url);
					InputStream i = u.openStream();
					byte[] b = new byte[1024*1024];
					int len;
					String fileName = url.substring(url.lastIndexOf("/"));
					OutputStream bos;
				
					bos = new FileOutputStream(new File(dir + fileName));
					while ((len = i.read(b)) != -1) {
							bos.write(b, 0, len);
						}
					bos.flush();
					bos.close();
					i.close();
					bean.setLocalFilePath(dir + fileName);
					resultList.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resultList;
		}
	
	/**
	 * 插入本地pdf的文件路径
	 */
	@SuppressWarnings({ "serial"})
	public void putData(List<EnterpriseReportBean> list){
		
		try {
			for (int i = 0; i < list.size(); i++) {
				EnterpriseReportBean bean = list.get(i);

				final Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put(cellName, bean.getLocalFilePath());
				List<Map<String, String>> temp = new ArrayList<Map<String, String>>() {{add(tempMap);}};

				Set<String> rowKeys = new TreeSet<String>();
				rowKeys.add(bean.getRowKey());
				HBaseAPP.putRecord(tableName, rowKeys, bean.getFamilyName(), temp);
			}
		} catch (Exception e) {
		}
	}
}
