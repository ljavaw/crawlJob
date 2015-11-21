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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeSet;

import com.dinfo.hbase.HBaseAPP;

public class EnterprisePDFDownload extends TimerTask{


	/**
	 * 下载pdf文件
	 */
	@Override
	public void run() {
		while (true) {
			List<EnterpriseReportBean> beans = getData();
			if (beans != null && beans.size() > 0) {
				List<EnterpriseReportBean> bean = downloadPDF(beans);
				putData(bean);
			} else {
				break;
			}
		}
	}

	/**
	 * 获得下载的url(默认一次只取100数据)
	 * 
	 * @return
	 */
	public List<EnterpriseReportBean> getData() {
		HBaseAPP app = new HBaseAPP();
		List<EnterpriseReportBean> list = app.scanRecordEnterprise(EnterpriseTableInfo.tableName,
				EnterpriseTableInfo.familyNames, EnterpriseTableInfo.cellName2, "0");
		return list;
	}

	/**
	 * 下载pdf文件到本地
	 */
	public List<EnterpriseReportBean> downloadPDF(List<EnterpriseReportBean> list){
		List<EnterpriseReportBean> resultList = new ArrayList<EnterpriseReportBean>();
				// 遍历pdf地址的list，逐个下载
			for(EnterpriseReportBean bean : list){
				String tempUrl = "";//用于测试
				try {
					String dir = "f:/pdf/";
					File file = new File(dir);
					if (!file.exists()) {
						file.mkdirs();
					}
					//上市交易所 0:上交所 1：深交所
					String url = "";
					if(bean.getExchange() != null && ("1").equals(bean.getExchange())){//深交所（所有）
						url = "http://disclosure.szse.cn"+bean.getRemoteFilePath().substring(2);
					}else if(bean.getExchange() != null && ("0").equals(bean.getExchange()) &&
							bean.getFamilyName() != null && ("annualReportInfo").equals(bean.getFamilyName())){//上交所——年报
						url = "http://static.sse.com.cn"+bean.getRemoteFilePath();
					}else if(bean.getExchange() != null && ("0").equals(bean.getExchange()) &&
							bean.getFamilyName() != null && ("noticeInfo").equals(bean.getFamilyName())){//上交所——公告
						url = bean.getRemoteFilePath().substring(bean.getRemoteFilePath().indexOf("http"));
					}else{
						continue;
					}
					tempUrl = url;
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
					bean.setLocalFilePath(dir + fileName);
					resultList.add(bean);
				} catch (MalformedURLException e) {
					System.out.println("url>>>>>>>>>>"+tempUrl);
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					System.out.println("url>>>>>>>>>>"+tempUrl);
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("url>>>>>>>>>>"+tempUrl);
					e.printStackTrace();
				}
			}
			return resultList;
	}

	/**
	 * 插入 "本地pdf文件路径" 和  改变"status"
	 */
	@SuppressWarnings({ "serial" })
	public void putData(List<EnterpriseReportBean> list) {

		try {
			for (int i = 0; i < list.size(); i++) {
				EnterpriseReportBean bean = list.get(i);

				final Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put(EnterpriseTableInfo.cellName1, bean.getLocalFilePath());
				// status 0:文件未下载1:文件已下载 2:已解析入库(未同步)
				tempMap.put(EnterpriseTableInfo.cellName2, "1");
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
}
