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
import java.util.List;
import java.util.TimerTask;

import com.dinfo.crawl.main.MainTask;
import com.dinfo.hbase.HBaseAPP;

public class EnterprisePDFDownload extends TimerTask{


	HBaseAPP app = new HBaseAPP();
	/**
	 * 下载pdf文件
	 */
	@Override
	public void run() {
		
		//status  0:文件未下载1:文件已下载2:已解析入库(未同步)
		String status = "0";	
		//一次查出的数据条数
		int pageSize = 100;
		try {
			while (true) {
				List<EnterpriseReportBean> beanList = app.scanRecordEnterprise(EnterpriseTableInfo.tableName,
						EnterpriseTableInfo.familyNames, EnterpriseTableInfo.cellName2, status, pageSize);
				System.out.println("PDF下载，查出的数据数：》》》"+beanList.size());
				if (beanList != null && beanList.size() > 0) {
					List<EnterpriseReportBean> beanResultList = downloadPDF(beanList);
					//插入 "本地pdf文件路径" 和  改变"status"
					HBaseAPP.updateRecord(EnterpriseTableInfo.tableName, beanResultList);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					String dir = MainTask.CRAWL_PROPERTIES.get("PDFfilepath");
					File file = new File(dir);
					if (!file.exists()) {
						file.mkdirs();
					}
					//上市交易所 0:上交所 1：深交所
					String url = "";
					if(bean.getExchange() != null && ("1").equals(bean.getExchange())){//深交所（所有）
						url = "http://disclosure.szse.cn"+bean.getRemoteFilePath().substring(2);
					}else if(bean.getExchange() != null && ("0").equals(bean.getExchange()) &&
							bean.getFamilyName() != null && (EnterpriseTableInfo.familyName_annualReportInfo).equals(bean.getFamilyName())){//上交所——年报
						url = "http://static.sse.com.cn"+bean.getRemoteFilePath();
					}else if(bean.getExchange() != null && ("0").equals(bean.getExchange()) &&
							bean.getFamilyName() != null && (EnterpriseTableInfo.familyName_noticeInfo).equals(bean.getFamilyName())){//上交所——公告
						url = bean.getRemoteFilePath().substring(bean.getRemoteFilePath().indexOf("http"));
					}else{
						continue;
					}
					tempUrl = url;
					System.out.println("url>>>>>>>>>>"+tempUrl);
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
					bean.setStatus("1");//用于改变信息状态
					resultList.add(bean);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return resultList;
	}
}
