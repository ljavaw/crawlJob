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
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.dinfo.crawl.enterprise.EnterpriseReportBean;
import com.dinfo.crawl.test.MyTest;

public class HBaseAPP {

	public static Configuration conf;

	public static List<byte[]> ROWKEYS = new ArrayList<byte[]>();// 插入数据用的（做关联）

	static {
		System.setProperty("hadoop.home.dir",
				MyTest.CRAWL_PROPERTIES.get("hadoop.home.dir"));
		conf = HBaseConfiguration.create();
		conf.set("hbase.rootdir", MyTest.CRAWL_PROPERTIES.get("hbase.rootdir"));
		conf.set("hbase.zookeeper.quorum",
				MyTest.CRAWL_PROPERTIES.get("hbase.zookeeper.quorum"));// 使用eclipse时必须添加这个，否则无法定位
		conf.set("hbase.zookeeper.property.clientPort", "2181");// 默认的是2181，不写这句也行
	}

	@SuppressWarnings({ "unused" })
	public static void main(String[] args) throws Exception {

		String table_name = "COMMUNITY_INFO_T";
		String family_name = "community";
		String index_table_name = "COMMUNITY_KEYS_INDEX";
		String index_family_name = "communityIndex";

		// createTable(index_table_name, index_family_name);
		// deleteTable(index_table_name);

		/*
		 * HandleDatabase hb = new HandleDatabase();//从mysql中查询数据 Util util =
		 * new Util(); MyTest.CRAWL_PROPERTIES = util.parseProperties();
		 * putRecord(table_name, family_name,hb.getCommunityData());
		 * putIndexTable(index_table_name, index_family_name, ROWKEYS);
		 */
		// getRecord(hTable);

		List<byte[]> rowkeyList = scanRecord(table_name, null, null);
		deleteRow(table_name, rowkeyList);
		/*
		 * List<String> rowkeyList = new ArrayList<String>(); Map<String,
		 * List<List<String>>> resultMap = scanRecord(table_name, null, null);
		 * Iterator it = resultMap.keySet().iterator(); while (it.hasNext()) {
		 * rowkeyList.add(it.next().toString()); } deleteRow(table_name,
		 * rowkeyList);
		 */

		// 输入格式yyyy-MM-dd HH:mm:ss
		// scanRecord(table_name, null,
		// String.valueOf(Util.getAssignTime("2015-05-26 17:30:00")));
		/*
		 * scanRecord(table_name, null, "1432634993371");
		 * System.out.println("=================="+System.currentTimeMillis());
		 */

	}

	/**
	 * 创建表
	 */
	@SuppressWarnings({ "deprecation", "resource" })
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
	@SuppressWarnings({ "deprecation", "resource" })
	public static void deleteTable(String table_name) throws IOException {
		// HBaseAdmin用来创建表 删除表使用 alt+shift+l 抽取变量
		HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);

		hBaseAdmin.disableTable(table_name);
		hBaseAdmin.deleteTable(table_name);
	}

	/*	*//**
	 * 扫面(可以指定开始和结束 行主键)(不指定就是查询全部)
	 */
	/*
	 * @SuppressWarnings("deprecation") public static Map<String,
	 * List<List<String>>> scanRecord(String table_name, String startRow, String
	 * endRow){
	 * 
	 * Map<String, List<List<String>>> resultMap = new HashMap<String,
	 * List<List<String>>>(); try { if(table_name != null &&
	 * !("").equals(table_name)){
	 * 
	 * HTable hTable = new HTable(conf, table_name); Scan scan = new Scan();
	 * if(startRow != null && !("").equals(startRow)){
	 * scan.setStartRow(Bytes.toBytes(startRow)); } if(endRow != null &&
	 * !("").equals(endRow)){ scan.setStopRow(Bytes.toBytes(endRow)); }
	 * ResultScanner scanner;
	 * 
	 * scanner = hTable.getScanner(scan);
	 * 
	 * for (Result result : scanner) { System.out.println("rowKey:" + new
	 * String(result.getRow()));
	 * 
	 * List<List<String>> stList = new ArrayList<List<String>>(); for (Cell cell
	 * : result.rawCells()) { System.out.println("列族:"+ new
	 * String(CellUtil.cloneFamily(cell)) + " 列:"+ new
	 * String(CellUtil.cloneQualifier(cell)) + " 值:"+ new
	 * String(CellUtil.cloneValue(cell))); List<String> infoList = new
	 * ArrayList<String>(); infoList.add(new
	 * String(CellUtil.cloneFamily(cell))); infoList.add(new
	 * String(CellUtil.cloneQualifier(cell))); infoList.add(new
	 * String(CellUtil.cloneValue(cell)));
	 * 
	 * stList.add(infoList); } resultMap.put(new String(result.getRow()),
	 * stList); } }else { System.out.println("表名不能为空！"); } } catch (IOException
	 * e) { System.out.println("IOException异常或目标表已不存在！"); e.printStackTrace(); }
	 * return resultMap; }
	 *//**
	 * (md5加密的)扫面(可以指定开始和结束 行主键)(不指定就是查询全部)
	 */
	@SuppressWarnings({ "deprecation", "resource" })
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
			byte[] temp = rowKey.getBytes();
			// ROWKEYS.add(temp);
			for (int i = 0; i < cellValue.size(); i++) {
				Map<String, String> map = cellValue.get(i);
				// String rowkey = UUID.randomUUID().toString();
				// byte[] temp = HbaseUtil.md5(rowkey).getBytes();

				Put put = new Put(temp);// rowkey

				Iterator iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					if (map.get(key) != null && !("").equals(map.get(key))) {
						put.addColumn(family_name.getBytes(),
								Bytes.toBytes(key), Bytes.toBytes(map.get(key)));
					}
				}
				hTable.put(put);
			}
		}
		hTable.close();
	}

	/*	*//**
	 * 根据 rowkey删除一条记录
	 */
	/*
	 * @SuppressWarnings({ "rawtypes", "unchecked", "deprecation", "resource" })
	 * public static void deleteRow(String table_name, List<String> rowkeylList)
	 * {
	 * 
	 * try { if(rowkeylList != null){ HTable hTable = new HTable(conf,
	 * table_name);
	 * 
	 * List list = new ArrayList(); for(String rowkey : rowkeylList){ Delete d1
	 * = new Delete(rowkey.getBytes()); list.add(d1); } hTable.delete(list);
	 * System.out.println("删除行完成!"); } } catch (IOException e) {
	 * e.printStackTrace(); } }
	 *//**
	 * 根据 rowkey删除一条记录
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation", "resource" })
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
	@SuppressWarnings({ "deprecation", "resource" })
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
					scan.setFilter(new SingleColumnValueFilter(family
							.getBytes(), qualifier.getBytes(), CompareOp.EQUAL,
							Bytes.toBytes("0")));
				}
				// scan.setFilter(new PageFilter(1000));
				ResultScanner scanner = hTable.getScanner(scan);

				for (Result result : scanner) {
					rowkeySet.add(new String(result.getRow()));

					for (Cell cell : result.rawCells()) {
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
	@SuppressWarnings({ "deprecation", "resource" })
	public Map<String, List<String>> getRecord(String tableName,
			Set<String> rowKeys, List<String> familyNames,
			List<String> cellNames) throws IOException {

		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		List<String> commentUrlList = new ArrayList<String>();// 社区表评论的url
		List<String> bbsUrlList = new ArrayList<String>();// 社区表bbs的url
		List<String> shopCommentUrlList = new ArrayList<String>();// 商户表中评论的url
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
				resultMap.put("commentUrls", commentUrlList);
				resultMap.put("bbsUrls", bbsUrlList);
				resultMap.put("shopCommentUrls", shopCommentUrlList);
			}
		}
		return resultMap;
	}

	/**
	 * scan查詢(企业数据表)
	 */
	@SuppressWarnings({ "deprecation", "resource" })
	public List<EnterpriseReportBean> scanRecordEnterprise(String table_name, List<String> familys,
					String qualifier, String qualifierUrl) {
		// ENTERPRIS_REPORT_T查询bean
		List<EnterpriseReportBean> reportBeanList = new ArrayList<EnterpriseReportBean>();

		try {
			if (table_name != null && !("").equals(table_name)) {

				HTable hTable = new HTable(conf, table_name);
				Scan scan = new Scan();
				// 参数为（列簇，列名，CompareOp.EQUAL，条件）各个条件之间是" and "的关系
				if (qualifier != null && !("").equals(qualifier)) {
					scan.setFilter(new SingleColumnValueFilter(familys.get(0)
							.getBytes(), qualifier.getBytes(), CompareOp.EQUAL,
							Bytes.toBytes("0")));
					scan.setFilter(new SingleColumnValueFilter(familys.get(1)
							.getBytes(), qualifier.getBytes(), CompareOp.EQUAL,
							Bytes.toBytes("0")));
				}
				scan.setFilter(new PageFilter(1000));
				ResultScanner scanner = hTable.getScanner(scan);

				for (Result result : scanner) {
					EnterpriseReportBean bean = new EnterpriseReportBean();
					bean.setRowKey(new String(result.getRow()));
					
					for (Cell cell : result.rawCells()) {
						if (qualifierUrl != null && !("").equals(qualifierUrl)
								&& qualifierUrl.equals(new String(CellUtil.cloneQualifier(cell)))){
							bean.setFamilyName(new String(CellUtil.cloneFamily(cell)));
							bean.setRemoteFilePath(new String(CellUtil.cloneValue(cell)));
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
	 * 查询指定数据(企业数据表)(暂时未用)
	 */
	@SuppressWarnings({ "deprecation", "resource" })
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
