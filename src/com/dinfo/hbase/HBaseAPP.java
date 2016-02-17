package com.dinfo.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.dinfo.crawl.enterprise.EnterpriseReportBean;
import com.dinfo.crawl.main.MainTask;

public class HBaseAPP {

	public static Configuration conf;

	public static List<byte[]> ROWKEYS = new ArrayList<byte[]>();// 插入数据用的（做关联）

	static {
		System.setProperty("hadoop.home.dir",
				MainTask.CRAWL_PROPERTIES.get("hadoop.home.dir"));
		conf = HBaseConfiguration.create();
		conf.set("hbase.rootdir", MainTask.CRAWL_PROPERTIES.get("hbase.rootdir"));
		conf.set("hbase.zookeeper.quorum",
				MainTask.CRAWL_PROPERTIES.get("hbase.zookeeper.quorum"));// 使用eclipse时必须添加这个，否则无法定位
		conf.set("hbase.zookeeper.property.clientPort", "2181");// 默认的是2181，不写这句也行
	}

	/*@SuppressWarnings({ "unused" })
	public static void main(String[] args) throws Exception {

		String table_name = "COMMUNITY_INFO_T";
		String family_name = "community";
		String index_table_name = "COMMUNITY_KEYS_INDEX";
		String index_family_name = "communityIndex";

		List<byte[]> rowkeyList = scanRecord(table_name, null, null);
		deleteRow(table_name, rowkeyList);
	}*/

	/**
	 * 创建表
	 */
	@SuppressWarnings({ "deprecation"})
	public static void createTable(String table_name, String family_name)
			throws IOException {
		// HBaseAdmin用来创建表 删除表使用 alt+shift+l 抽取变量
		HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);

		if (!(hBaseAdmin.tableExists(table_name))) {
			HTableDescriptor hTableDescriptor = new HTableDescriptor(table_name);
			HColumnDescriptor family = new HColumnDescriptor(family_name);
			hTableDescriptor.addFamily(family);
			hBaseAdmin.createTable(hTableDescriptor);
		}
	}

	/**
	 * 删除表
	 */
	@SuppressWarnings({ "deprecation"})
	public static void deleteTable(String table_name) throws IOException {
		// HBaseAdmin用来创建表 删除表使用 alt+shift+l 抽取变量
		HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);

		hBaseAdmin.disableTable(table_name);
		hBaseAdmin.deleteTable(table_name);
	}

	@SuppressWarnings({ "deprecation"})
	public static List<byte[]> scanRecord(String table_name, String startRow,
			String endRow) {

		List<byte[]> resultList = new ArrayList<byte[]>();
		try {
			if (table_name != null && !("").equals(table_name)) {

				HTable hTable = new HTable(conf, table_name);
				Scan scan = new Scan();
				if (startRow != null && !("").equals(startRow)) {
					scan.setStartRow(Bytes.toBytes(startRow));
				}
				if (endRow != null && !("").equals(endRow)) {
					scan.setStopRow(Bytes.toBytes(endRow));
				}
				ResultScanner scanner = hTable.getScanner(scan);
				for (Result result : scanner) {
					resultList.add(result.getRow());
				}
			} else {
				System.out.println("表名不能为空！");
			}
		} catch (IOException e) {
			System.out.println("IOException异常或目标表已不存在！");
			e.printStackTrace();
		}
		return resultList;
	}

	/**
	 * 插入索引数据表
	 */
	@SuppressWarnings({ "deprecation" })
	public static void putIndexTable(String table_name, String family_name,
			List<byte[]> cellValue) throws IOException {

		HTable hTable = new HTable(conf, table_name);
		for (int i = 0; i < cellValue.size(); i++) {

			Put put = new Put(Bytes.toBytes(String.valueOf(System
					.currentTimeMillis())));// rowkey
			put.addColumn(family_name.getBytes(),
					Bytes.toBytes("communitykey"), cellValue.get(i));
			hTable.put(put);
		}
		hTable.close();
	}

	/**
	 * 插入数据
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static void putRecord(String table_name, Set<String> rowKeys,
			String family_name, List<Map<String, String>> cellValue)
			throws IOException {

		HTable hTable = new HTable(conf, table_name);
		for (String rowKey : rowKeys) {
			byte[] rowkey = rowKey.getBytes();
			for (int i = 0; i < cellValue.size(); i++) {
				Map<String, String> map = cellValue.get(i);
				Put put = new Put(rowkey);// rowkey

				Iterator iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = map.get(key);
					if ( value != null && !("").equals(value)) {
						put.addColumn(family_name.getBytes(),
								Bytes.toBytes(key), Bytes.toBytes(value));
					}
				}
				hTable.put(put);
			}
		}
		hTable.close();
	}
	/*
	 * 根据 rowkey删除一条记录
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation"})
	public static void deleteRow(String table_name, List<byte[]> rowkeyList) {

		try {
			if (rowkeyList != null) {
				HTable hTable = new HTable(conf, table_name);

				List list = new ArrayList();
				for (byte[] rowkey : rowkeyList) {
					Delete d1 = new Delete(rowkey);
					list.add(d1);
				}
				hTable.delete(list);
				System.out.println("删除行完成!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询索引表
	 */
	@SuppressWarnings({ "deprecation"})
	public Map<String, Set<?>> getIndexInfo(String table_name, String family,
			String qualifier, String qualifierUrl) {

		Map<String, Set<?>> map = new HashMap<String, Set<?>>();

		Set<String> rowkeySet = new HashSet<String>();
		Set<String> cellSet = new HashSet<String>();
		try {
			if (table_name != null && !("").equals(table_name)) {

				HTable hTable = new HTable(conf, table_name);
				Scan scan = new Scan();
				// 参数为（列簇，列名，CompareOp.EQUAL，条件）各个条件之间是" and "的关系
				if (qualifier != null && !("").equals(qualifier)) {
					scan.setFilter(new SingleColumnValueFilter(family.getBytes(), 
							qualifier.getBytes(), CompareOp.EQUAL, 
							Bytes.toBytes("0")));
				}
				ResultScanner scanner = hTable.getScanner(scan);
				//分页默认5000条
				for (Result result : scanner.next(2000)) {
					rowkeySet.add(new String(result.getRow()));

					for (Cell cell : result.rawCells()) {
//						if(("dispost_status").equals(new String(CellUtil.cloneQualifier(cell)))){
//							System.out.println(new String(CellUtil.cloneQualifier(cell))+"::"+new String(CellUtil.cloneValue(cell)));
//						}
						if (qualifierUrl != null
								&& !("").equals(qualifierUrl)
								&& qualifierUrl.equals(new String(CellUtil
										.cloneQualifier(cell)))) {
							cellSet.add(new String(CellUtil.cloneValue(cell)));
						}
					}
				}
				map.put("rowKeys", rowkeySet);
				map.put("cells", cellSet);
			} else {
				System.out.println("表名不能为空！");
			}
		} catch (IOException e) {
			System.out.println("IOException异常或目标表已不存在！");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 查询指定数据
	 */
	@SuppressWarnings({ "deprecation"})
	public Map<String, List<String>> getRecord(String tableName,
			Set<String> rowKeys, List<String> familyNames,
			List<String> cellNames) throws IOException {

		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		List<String> commentUrlList = new ArrayList<String>();// 社区表评论的url
		List<String> bbsUrlList = new ArrayList<String>();// 社区表bbs的url
		List<String> shopCommentUrlList = new ArrayList<String>();// 商户表中评论的url
		if (tableName != null && !("").equals(tableName)) {
			HTable hTable = new HTable(conf, tableName);
			List<Get> getList = new ArrayList<Get>();
			for(String rowKey : rowKeys){
				Get get = new Get(rowKey.getBytes());
				getList.add(get);
			}
			Result[] resultArray = hTable.get(getList);
			for (Result result : resultArray) {
				for (String familyName : familyNames) {
					for (String cellName : cellNames) {
						byte[] value = result.getValue(familyName.getBytes(),
								cellName.getBytes());
						if (value != null) {
							if ("comment_url".equals(new String(cellName))) {
								commentUrlList.add(new String(value));
							}
							if ("bbs_url".equals(new String(cellName))) {
								bbsUrlList.add(new String(value));
							}
							if ("url".equals(new String(cellName))) {
								shopCommentUrlList.add(new String(value));
							}
						}
					}
				}
//				resultMap.put("commentUrls", commentUrlList);
				resultMap.put("bbsUrls", bbsUrlList);
				resultMap.put("shopCommentUrls", shopCommentUrlList);
			}
		}
		return resultMap;
	}
	
	/**
	 * 查询指定数据(解析html时用)
	 */
	@SuppressWarnings({ "deprecation"})
	public Map<String, Map<String, String>> getRecordForParase(String tableName, Set<String> rowKeys, List<String> familyNames,
			List<String> cellNames, String province, String city) throws IOException {
		//<rowkeyName,<fieldName,fieldValue>>
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		if (tableName != null && !("").equals(tableName)) {
			HTable hTable = new HTable(conf, tableName);
			List<Get> getList = new ArrayList<Get>();
			for (String rowKey : rowKeys) {
				Get get = new Get(rowKey.getBytes());
				getList.add(get);
			}
			Result[] resultArray = hTable.get(getList);
			for(Result result : resultArray){
				Map<String, String> fieldMap = new HashMap<String, String>();// 原始页面存放路径filePath
				for (String familyName : familyNames) {
					for (String cellName : cellNames) {
						byte[] value = result.getValue(familyName.getBytes(),
								cellName.getBytes());
						if (value != null) {
							System.out.println("列族:" + new String(familyName)
							+ " 列:" + new String(cellName) + " 值:"
							+ new String(value));
							
							if ("province".equals(new String(cellName)) ) {
								fieldMap.put("province", new String(value));
							}
							if ("city".equals(new String(cellName)) ) {
								fieldMap.put("city", new String(value));
							}
							if ("filePath".equals(new String(cellName))) {
								fieldMap.put("filePath", new String(value));
							}
						}
					}
				}
				resultMap.put(new String(result.getRow()), fieldMap);
			}
		}
		return resultMap;
	}

	/**
	 * scan查詢(企业数据表)
	 */
	@SuppressWarnings({ "deprecation"})
	public List<EnterpriseReportBean> scanRecordEnterprise(String table_name, List<String> familys,
					String qualifierStatus, String qualifierStatusVal, int pageSize) {
		// ENTERPRIS_REPORT_T查询bean
		List<EnterpriseReportBean> reportBeanList = new ArrayList<EnterpriseReportBean>();

		try {
			if (table_name != null && !("").equals(table_name)) {

				HTable hTable = new HTable(conf, table_name);
				Scan scan = new Scan();
//				scan.setFilter(new PageFilter(pageSize));
				// 参数为（列簇，列名，CompareOp.EQUAL，条件）各个条件之间是" and "的关系
				if (qualifierStatus != null && !("").equals(qualifierStatus)) {
					SingleColumnValueFilter filter1 = new SingleColumnValueFilter(familys.get(0)
							.getBytes(), Bytes.toBytes(qualifierStatus), CompareOp.EQUAL,
							Bytes.toBytes(qualifierStatusVal)); 
					filter1.setFilterIfMissing(true);
					SingleColumnValueFilter  filter2 = new SingleColumnValueFilter(familys.get(1)
							.getBytes(), qualifierStatus.getBytes(), CompareOp.EQUAL,
							Bytes.toBytes(qualifierStatusVal)); 
					filter2.setFilterIfMissing(true);
					scan.setFilter(filter1);
					scan.setFilter(filter2);
				}
				scan.setMaxResultSize(1l);
				ResultScanner scanner = hTable.getScanner(scan);

				for (Result result : scanner.next(pageSize)) {
					EnterpriseReportBean bean = new EnterpriseReportBean();
					bean.setRowKey(new String(result.getRow()));
					
					for (Cell cell : result.rawCells()) {
//						System.out.println(new String(CellUtil.cloneQualifier(cell))+"  "+new String(CellUtil.cloneValue(cell)));
						if(("noticeInfo").equals(new String(CellUtil.cloneFamily(cell)))){
							switch(new String(CellUtil.cloneQualifier(cell))){
							case "remoteFilePath":bean.setRemoteFilePath(new String(CellUtil.cloneValue(cell)));
												  bean.setFamilyName(new String(CellUtil.cloneFamily(cell)));break;
							case "localFilePath":bean.setLocalFilePath(new String(CellUtil.cloneValue(cell)));break;
							case "getTime":bean.setGetTime(new String(CellUtil.cloneValue(cell)));break;
							case "exchange":bean.setExchange(new String(CellUtil.cloneValue(cell)));break;
							case "status":bean.setStatus(new String(CellUtil.cloneValue(cell)));break;
							case "noticeTitle":bean.setTitle(new String(CellUtil.cloneValue(cell)));break;
							case "noticeDate":bean.setReleaseTime(new String(CellUtil.cloneValue(cell)));break;
							case "stockCode":bean.setStockCode(new String(CellUtil.cloneValue(cell)));break;
							}
							
						}else if(("annualReportInfo").equals(new String(CellUtil.cloneFamily(cell)))){
							switch(new String(CellUtil.cloneQualifier(cell))){
								case "remoteFilePath":bean.setRemoteFilePath(new String(CellUtil.cloneValue(cell)));
													  bean.setFamilyName(new String(CellUtil.cloneFamily(cell)));break;
								case "localFilePath":bean.setLocalFilePath(new String(CellUtil.cloneValue(cell)));break;
								case "getTime":bean.setGetTime(new String(CellUtil.cloneValue(cell)));break;
								case "exchange":bean.setExchange(new String(CellUtil.cloneValue(cell)));break;
								case "status":bean.setStatus(new String(CellUtil.cloneValue(cell)));break;
								case "annualReportTitle":bean.setTitle(new String(CellUtil.cloneValue(cell)));break;
								case "reportDate":bean.setReleaseTime(new String(CellUtil.cloneValue(cell)));break;
								case "stockCode":bean.setStockCode(new String(CellUtil.cloneValue(cell)));break;
							}
						}
					}
					reportBeanList.add(bean);
				}
			} else {
				System.out.println("表名不能为空！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reportBeanList;
	}
	
	/**
	 * 更新数据
	 */
	@SuppressWarnings({ "deprecation"})
	public static void updateRecord(String table_name, List<EnterpriseReportBean> list)
			throws IOException {

		HTable hTable = new HTable(conf, table_name);
		for (EnterpriseReportBean bean : list) {
			byte[] rowkey = bean.getRowKey().getBytes();
			Put put = new Put(rowkey);// rowkey
			if("noticeInfo".equals(bean.getFamilyName())){
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("remoteFilePath"), Bytes.toBytes(bean.getRemoteFilePath()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("localFilePath"), Bytes.toBytes(bean.getLocalFilePath()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("getTime"), Bytes.toBytes(bean.getGetTime()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("exchange"), Bytes.toBytes(bean.getExchange()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("status"), Bytes.toBytes(bean.getStatus()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("noticeTitle"), Bytes.toBytes(bean.getTitle()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("noticeDate"), Bytes.toBytes(bean.getReleaseTime()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("stockCode"), Bytes.toBytes(bean.getStockCode()));
			}else if("annualReportInfo".equals(bean.getFamilyName())){
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("remoteFilePath"), Bytes.toBytes(bean.getRemoteFilePath()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("localFilePath"), Bytes.toBytes(bean.getLocalFilePath()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("getTime"), Bytes.toBytes(bean.getGetTime()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("exchange"), Bytes.toBytes(bean.getExchange()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("status"), Bytes.toBytes(bean.getStatus()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("annualReportTitle"), Bytes.toBytes(bean.getTitle()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("reportDate"), Bytes.toBytes(bean.getReleaseTime()));
				put.addColumn(bean.getFamilyName().getBytes(), Bytes.toBytes("stockCode"), Bytes.toBytes(bean.getStockCode()));
			}
			hTable.put(put);
		}
		hTable.close();
	}

	/**
	 * 查询指定数据(企业数据表)(暂时未用)
	 */
	@SuppressWarnings({ "deprecation"})
	public List<EnterpriseReportBean> getRecordEnterprise(String tableName,
			Set<String> rowKeys, List<String> familyNames,
			List<String> cellNames) throws IOException {

		// ENTERPRIS_REPORT_T查询bean
		List<EnterpriseReportBean> reportBeanList = new ArrayList<EnterpriseReportBean>();
		if (tableName != null && !("").equals(tableName)) {
			HTable hTable = new HTable(conf, tableName);
			for (String rowKey : rowKeys) {
				Get get = new Get(rowKey.getBytes());
				Result result = hTable.get(get);
				for (String familyName : familyNames) {
					for (String cellName : cellNames) {
						byte[] value = result.getValue(familyName.getBytes(),
								cellName.getBytes());
						if (value != null) {
							System.out.println("列族:" + new String(familyName)
									+ " 列:" + new String(cellName) + " 值:"
									+ new String(value));
							// status 0:文件未下载1:文件已下载 2:已解析入库(未同步)
							if ("status".equals(new String(cellName))
									&& "0".equals(new String(value))) {
								for (String cellNameTemp : cellNames) {
									if ("remoteFilePath".equals(new String(
											cellNameTemp))) {
										byte[] valueTemp = result.getValue(
												familyName.getBytes(),
												cellNameTemp.getBytes());
										EnterpriseReportBean bean = new EnterpriseReportBean();
										bean.setRowKey(rowKey);
										bean.setFamilyName(familyName);
										bean.setRemoteFilePath(new String(
												valueTemp));
										reportBeanList.add(bean);
									}
								}
							}
						}
					}
				}
			}
		}
		return reportBeanList;
	}
}
