package com.dinfo.crawl.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dinfo.crawl.bean.SourceExcelBean;
import com.dinfo.crawl.bean.TableInfo;
import com.dinfo.crawl.bean.UrlAndTemplateBean;
import com.dinfo.crawl.conf.JobConfig;
import com.dinfo.hbase.HBaseAPP;
/**
 * @author ljavaw
 */
public class HandleDatabase {

	public Connection conn;
	public PreparedStatement pstm1;
	public PreparedStatement pstm2;
	public ResultSet rs1;
	public ResultSet rs2;
	
	public HandleDatabase() {
		super();
	}
	public HandleDatabase(Connection conn, PreparedStatement pstm1,
			PreparedStatement pstm2, ResultSet rs1, ResultSet rs2) {
		super();
		this.conn = conn;
		this.pstm1 = pstm1;
		this.pstm2 = pstm2;
		this.rs1 = rs1;
		this.rs2 = rs2;
	}
	/**
	 * query data1
	 */
	public List<UrlAndTemplateBean> getData1(String querySql) {
		
		List<UrlAndTemplateBean> beans = new ArrayList<UrlAndTemplateBean>();
		
		try {
			pstm1 = conn.prepareStatement(querySql);
			rs1 = pstm1.executeQuery();
			
			while(rs1.next()){
				UrlAndTemplateBean bean = new UrlAndTemplateBean();
				Integer id = rs1.getInt("id");
				String province = rs1.getString("province");
				String city = rs1.getString("city");
				String url = rs1.getString("url");
				Integer page_num = rs1.getInt("page_num");
				String template_content = rs1.getString("template_content");
				
				bean.setId(id);
				bean.setSourceProvice(province);
				bean.setSourceCity(city);
				bean.setTemplate_content_source(template_content);
				bean.setPage_num(page_num);
				bean.setSourceUrl(url);
				beans.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return beans;
	}
	/**
	 * query data2
	 * @throws IOException 
	 */
	@SuppressWarnings({"unchecked" })
	public UrlAndTemplateBean getData22(String tableName, String querySql2) throws IOException {
		
		UrlAndTemplateBean bean = new UrlAndTemplateBean();
		HBaseAPP app = new HBaseAPP();
		try {
			String indexTableName = "";
			List<String> familyNames = null;
			List<String> cellNames = null;
			switch (tableName){
				  case TableInfo.COMMUNITY_COMMENT_INFO :  familyNames = TableInfo.COMMUNITY_COMMENT_INFO_FAMILYNAME_LIST; 
				  cellNames = TableInfo.COMMUNITY_COMMENT_INFO_CELL_LIST;
				  indexTableName = TableInfo.COMMUNITY_COMMENT_INDEX;
				  tableName = TableInfo.COMMUNITY_COMMENT_INFO;
				  break;
				  case TableInfo.COMMUNITY_BBS_INFO :  familyNames = TableInfo.COMMUNITY_BBS_INFO_FAMILYNAME_LIST; 
				  cellNames = TableInfo.COMMUNITY_BBS_INFO_CELL_LIST;
				  indexTableName = TableInfo.COMMUNITY_BBS_INDEX;
				  tableName = TableInfo.COMMUNITY_BBS_INFO;
				  break;
				  case TableInfo.COMMUNITY_INFO_T :  familyNames = TableInfo.COMMUNITY_INFO_T_FAMILYNAME_LIST; 
				  cellNames = TableInfo.COMMUNITY_INFO_T_CELL_LIST;
				  indexTableName = TableInfo.COMMUNITY_KEYS_INDEX;
				  tableName = TableInfo.COMMUNITY_INFO_T;
				  break;
				  case TableInfo.MERCHANT_COMMENT_INFO :  familyNames = TableInfo.MERCHANT_COMMENT_INFO_FAMILYNAME_LIST; 
				  cellNames = TableInfo.MERCHANT_COMMENT_INFO_CELL_LIST;
				  indexTableName = TableInfo.MERCHANT_COMMENT_INDEX;
				  tableName = TableInfo.MERCHANT_COMMENT_INFO;
				  break;
				  case TableInfo.MERCHANT_INFO_T :  familyNames = TableInfo.MERCHANT_INFO_T_FAMILYNAME_LIST; 
				  cellNames = TableInfo.MERCHANT_INFO_T_CELL_LIST;
				  indexTableName = TableInfo.MERCHANT_KEYS_INDEX;
				  tableName = TableInfo.MERCHANT_INFO_T;
				  break;
				  case TableInfo.SHOP_INFO_T :  familyNames = TableInfo.SHOP_INFO_T_FAMILYNAME_LIST; 
				  cellNames = TableInfo.SHOP_INFO_T_CELL_LIST;
				  //indexTableName = TableInfo.SHOP_INFO_INDEX;
				  tableName = TableInfo.SHOP_INFO_T;
				  break;
			}
			
			Map<String, Set<?>> map = app.getIndexInfo(indexTableName, "communityIndex", "dispost_status","communitykey");
			Set<String> rowkeySet = (Set<String>) map.get("rowKeys");//用于改变采集状态
			Set<String> cellSet = (Set<String>) map.get("cells");//用于查询数据表
			if(rowkeySet != null && rowkeySet.size() > 0 &&
					cellSet != null && cellSet.size() > 0){
				bean.setRowKeys(rowkeySet);
				
				//索引表的值就是数据表的rowkey
				Map<String, List<String>> dataMap = app.getRecord(tableName, cellSet, familyNames, cellNames);
				bean.setBbsUrlList(dataMap.get("bbsUrls"));
				bean.setCommentUrlList(dataMap.get("commentUrls"));
				
				//template_info_t
				pstm2 = conn.prepareStatement(querySql2);
				rs2 = pstm2.executeQuery();
				
				String template_content_b = "";
				String template_content_c = "";
				
				int i = 1;
				while(rs2.next()){
					if (i == 1){
						template_content_c = rs2.getString("template_content");
					} else {
						template_content_b = rs2.getString("template_content");
					}
					i++;
				}
				bean.setTemplate_content_bbs(template_content_b);
				bean.setTemplate_content_comment(template_content_c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return bean;
	}
	/**
	 * query data2
	 */
	public UrlAndTemplateBean getData2(String querySql1, String querySql2) {
		
		UrlAndTemplateBean bean = new UrlAndTemplateBean();
		
		try {
			
			//community_info_t
			pstm1 = conn.prepareStatement(querySql1);
			rs1 = pstm1.executeQuery();
			
			List<Integer> ids = new ArrayList<Integer>();
			List<String> urls1 = new ArrayList<String>();
			List<String> urls2 = new ArrayList<String>();
			while(rs1.next()){
				Integer id = rs1.getInt("id");
				String bbsUrl = rs1.getString("bbs_url");
				String commentUrl = rs1.getString("comment_url");
				ids.add(id);
				urls1.add(bbsUrl);
				urls2.add(commentUrl);
			}
			rs1.last();//将指针移动到结果集的最后一条记录
			bean.setCommentUrlList(urls2);
			bean.setPage_num(rs1.getRow());//url的条数
			
			bean.setIds(ids);
			bean.setBbsUrlList(urls1);
			bean.setCommentUrlList(urls2);
			
			//template_info_t
			pstm2 = conn.prepareStatement(querySql2);
			rs2 = pstm2.executeQuery();
			
			String template_content_b = "";
			String template_content_c = "";
			
			int i = 1;
			while(rs2.next()){
				if (i == 1){
					template_content_c = rs2.getString("template_content");
				} else {
					template_content_b = rs2.getString("template_content");
				}
				i++;
			}
			bean.setTemplate_content_bbs(template_content_b);
			bean.setTemplate_content_comment(template_content_c);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return bean;
	}
	/**
	 * query data33
	 */
	@SuppressWarnings("unchecked")
	public UrlAndTemplateBean getData33(String tableName,String sql) {
		
		UrlAndTemplateBean bean = new UrlAndTemplateBean();
		HBaseAPP app = new HBaseAPP();
		try {
			String indexTableName = "";
			List<String> familyNames = null;
			List<String> cellNames = null;
			switch (tableName){
				  case TableInfo.COMMUNITY_COMMENT_INFO :  familyNames = TableInfo.COMMUNITY_COMMENT_INFO_FAMILYNAME_LIST; 
				  cellNames = TableInfo.COMMUNITY_COMMENT_INFO_CELL_LIST;
				  indexTableName = TableInfo.COMMUNITY_COMMENT_INDEX;
				  tableName = TableInfo.COMMUNITY_COMMENT_INFO;
				  break;
				  case TableInfo.COMMUNITY_BBS_INFO :  familyNames = TableInfo.COMMUNITY_BBS_INFO_FAMILYNAME_LIST; 
				  cellNames = TableInfo.COMMUNITY_BBS_INFO_CELL_LIST;
				  indexTableName = TableInfo.COMMUNITY_BBS_INDEX;
				  tableName = TableInfo.COMMUNITY_BBS_INFO;
				  break;
				  case TableInfo.COMMUNITY_INFO_T :  familyNames = TableInfo.COMMUNITY_INFO_T_FAMILYNAME_LIST; 
				  cellNames = TableInfo.COMMUNITY_INFO_T_CELL_LIST;
				  indexTableName = TableInfo.COMMUNITY_KEYS_INDEX;
				  tableName = TableInfo.COMMUNITY_INFO_T;
				  break;
				  case TableInfo.MERCHANT_COMMENT_INFO :  familyNames = TableInfo.MERCHANT_COMMENT_INFO_FAMILYNAME_LIST; 
				  cellNames = TableInfo.MERCHANT_COMMENT_INFO_CELL_LIST;
				  indexTableName = TableInfo.MERCHANT_COMMENT_INDEX;
				  tableName = TableInfo.MERCHANT_COMMENT_INFO;
				  break;
				  case TableInfo.MERCHANT_INFO_T :  familyNames = TableInfo.MERCHANT_INFO_T_FAMILYNAME_LIST; 
				  cellNames = TableInfo.MERCHANT_INFO_T_CELL_LIST;
				  indexTableName = TableInfo.MERCHANT_KEYS_INDEX;
				  tableName = TableInfo.MERCHANT_INFO_T;
				  break;
				  case TableInfo.SHOP_INFO_T :  familyNames = TableInfo.SHOP_INFO_T_FAMILYNAME_LIST; 
				  cellNames = TableInfo.SHOP_INFO_T_CELL_LIST;
				  indexTableName = TableInfo.SHOP_INFO_INDEX;
				  tableName = TableInfo.SHOP_INFO_T;
				  break;
			}
			//dispost_status:0,未创建采集job;1,已经创建采集job(未同步);
			Map<String, Set<?>> map = app.getIndexInfo(indexTableName, "shopInfoIndex", "dispost_status", "shopkeys");
			Set<String> rowkeySet = (Set<String>) map.get("rowKeys");//用于改变采集状态
			Set<String> cellSet = (Set<String>) map.get("cells");//用于查询数据表
			
			if(rowkeySet != null && rowkeySet.size() > 0 &&
					cellSet != null && cellSet.size() > 0){
				bean.setRowKeys(rowkeySet);
				
				//索引表的值就是数据表的rowkey
				Map<String, List<String>> dataMap = app.getRecord(tableName, cellSet, familyNames, cellNames);
				bean.setShopCommentUrlList(dataMap.get("shopCommentUrls"));
				
				//template_info_t
				pstm2 = conn.prepareStatement(sql);
				rs2 = pstm2.executeQuery();
				
				String template_content_shop = "";
				
				while(rs2.next()){
					template_content_shop = rs2.getString("template_content");
				}
				bean.setTemplate_content_shop(template_content_shop);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return bean;
	}
	
	/**
	 * query data3
	 */
	public UrlAndTemplateBean getData3(String querySql1) {
		
		UrlAndTemplateBean bean = new UrlAndTemplateBean();
		try {
			
			//merchant
			pstm1 = conn.prepareStatement(querySql1);
			rs1 = pstm1.executeQuery();
			
			String template_content = "";
			List<Integer> ids = new ArrayList<Integer>();
			List<String> urls2 = new ArrayList<String>();
			Set<String> urls1 = new HashSet<String>();
			while(rs1.next()){
				template_content = rs1.getString("template_content");
				Integer id = rs1.getInt("id");
				String commentUrl = rs1.getString("url");
				urls1.add(commentUrl);
				ids.add(id);
			}
			rs1.last();//将指针移动到结果集的最后一条记录
			bean.setTemplate_content_shop(template_content);
			bean.setPage_num(rs1.getRow());//url的条数
			bean.setIds(ids);
			urls2.addAll(urls1);
			bean.setShopCommentUrlList(urls2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * add data(xml module info)
	 */
	@SuppressWarnings("static-access")
	public void addInfo(String template_name, String file_path, int platform_type) {
		Util util = new Util();
		
		String insertSql = "INSERT INTO template_info_t (template_name, template_content, file_path, platform_type)VALUES(?,?,?,?)";
		String template_content = util.xmlToString(file_path);
		try {
			pstm1 = conn.prepareStatement(insertSql);
			pstm1.setString(1, template_name);
			pstm1.setString(2, template_content);
			pstm1.setString(3, file_path);
			pstm1.setInt(4, platform_type);
			pstm1.execute();
			System.out.println("insert template_info_t the data successfully！");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * add data(xml config info)
	 * 含有blob字段类型
	 */
	@SuppressWarnings("rawtypes")
	public void addConfigInfo(Map<String, Map<String, JobConfig>> addConfigMap) {
		
		try {
			String insertSql = "INSERT INTO config_info_t (name, config, url_xml)VALUES(?,?,?)";
			pstm1 = conn.prepareStatement(insertSql);
			conn.setAutoCommit(false);
			Iterator it = addConfigMap.keySet().iterator();
			//添加到数据库
			while (it.hasNext()) {
				String name = it.next().toString();
				Map<String, JobConfig> resultMap = addConfigMap.get(name);
				Iterator temp = resultMap.keySet().iterator();
				while (temp.hasNext()) {
					String tempName = temp.next().toString();
					pstm1.setString(1, tempName);//job名
					pstm1.setObject(2, resultMap.get(tempName));//job对象
					pstm1.setString(3, name);//xml字符串（只含有一个job）
					pstm1.addBatch();
				}
			}
			pstm1.executeBatch();
			conn.commit();
			System.out.println("insert the config data successfully！");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * add data(excel source info)
	 */
	public void addSourceInfo(List<SourceExcelBean> sebList) {
		String insertSql = "INSERT INTO source_info_t (province, city, country, url, page_num, template_id, platform_type)VALUES(?,?,?,?,?,?,?)";
		try {
			if(sebList != null && sebList.size() > 0){
				conn = HandleDatabase.getConnection();
				pstm1 = conn.prepareStatement(insertSql);
				conn.setAutoCommit(false);
				for(int i=0; i<sebList.size(); i++){
					pstm1.setString(1, sebList.get(i).getProvinceExcel());
					pstm1.setString(2, sebList.get(i).getCityExcel());
					pstm1.setString(3, sebList.get(i).getAreaExcel());
					pstm1.setString(4, sebList.get(i).getUrlExcel());
					pstm1.setString(5, sebList.get(i).getAllPageExcel());
					pstm1.setInt(6, sebList.get(i).getTemplateIdExcel());
					pstm1.setInt(7, sebList.get(i).getPlatformTypeIdExcel());
					pstm1.addBatch();
				}
				pstm1.executeBatch();
				conn.commit();
				System.out.println("insert the source data successfully！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			HandleDatabase.closeDataSource(rs1, rs2, pstm1, pstm2, conn);
		}
	}
	/**
	 * query data
	 */
	public Map<String, JobConfig> getConfigData() {
		
		Map<String, JobConfig> infoMap = new HashMap<String, JobConfig>();
		try {
			pstm1 = conn.prepareStatement("SELECT * FROM config_info_t");
			rs1 = pstm1.executeQuery();
			while(rs1.next()){
				
				String name = rs1.getString("name");
				
				//读取blob字段中的数据
				Blob inBlob = (Blob) rs1.getBlob("config");
				InputStream is = inBlob.getBinaryStream();
				BufferedInputStream input = new BufferedInputStream(is);
				byte[] buff = new byte[(int) inBlob.length()];
				
				JobConfig jobConfig = null;
				while (-1 != input.read(buff, 0, buff.length)) {
					ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buff));
					jobConfig = (JobConfig) in.readObject();
				}
				
				infoMap.put(name, jobConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return infoMap;
	}
	/**
	 * hbase中的数据
	 * 参数List<Object> 可能为string 类型的 url
	 * update collect_state
	 */
	@SuppressWarnings({ "static-access"})
	public void updateField(String indexTableName,String familyName, String cellName, Set<String> rowKeys) {
		try {
			HBaseAPP app = new HBaseAPP();
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			
			map.put(cellName, "1");
			list.add(map);
			app.putRecord(indexTableName, rowKeys, familyName, list);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 参数List<Object> 可能为string 类型的 url
	 * update collect_state
	 */
	public void updateField(String updateSql, List<Integer> ids) {
		
		try {
			pstm1 = conn.prepareStatement(updateSql);
			conn.setAutoCommit(false);
			for (Object id : ids) {
				pstm1.setObject(1, id);
				pstm1.addBatch();
			}
			pstm1.executeBatch();
			conn.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	public static Connection getConnection(){  
		 try {
			
	            String drivers =  MyTest.CRAWL_PROPERTIES.get("driverclass");  
	            if (drivers != null && !"".equals(drivers)) {  
	                System.setProperty("driverclass", drivers);  
	            }  
	            String url = MyTest.CRAWL_PROPERTIES.get("dburl");  
	            String username = MyTest.CRAWL_PROPERTIES.get("username");  
	            String password = MyTest.CRAWL_PROPERTIES.get("password");  
           
				return DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				e.printStackTrace();
			}  
		 return null;
    }  
	
	/**
	 * query data  community_info_t
	 */
	public List<Map<String, String>> getCommunityData() {
		
		Connection conn = HandleDatabase.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Map<String, String>> infoMap = new ArrayList<Map<String, String>>();
		try {
			ps = conn.prepareStatement("SELECT * FROM community_info_t");
			rs = ps.executeQuery();
			int z = 0;
			while(rs.next()){
				if(z == 2){
					break;
				}
				Map<String, String> map = new HashMap<String, String>();
				ResultSetMetaData rsmd = rs.getMetaData(); 
				int columnCount = rsmd.getColumnCount();//总列数
				
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
				}
				infoMap.add(map);
				z ++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			closeDataSource(rs, null, ps, null, conn);
		}
		return infoMap;
	}
	/**
	 * 得到表中的数据数
	 */
	public int getCountData(String sql) {
		
		Connection conn = HandleDatabase.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			count = rs.getRow();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			closeDataSource(rs, null, ps, null, conn);
		}
		return count;
	}
	
	/**
	 * 关闭资源连接
	 */
	public static void closeDataSource(ResultSet rs1,ResultSet rs2, PreparedStatement pstmt1, PreparedStatement pstmt2,Connection conn){
		 	try {
	            if (rs1!=null) {//关闭结果集对象
	                rs1.close();
	            }
	        } catch (Exception e) {
	            
	            e.printStackTrace();
	        }
		 	try {
		 		if (rs2!=null) {//关闭结果集对象
		 			rs2.close();
		 		}
		 	} catch (Exception e) {
		 		
		 		e.printStackTrace();
		 	}
	        
	        try {
	            if (pstmt1!=null) {
	                pstmt1.close();//关闭预编译对象1
	            }
	        } catch (Exception e) {
	            
	            e.printStackTrace();
	        }
	        try {
	        	if (pstmt2!=null) {
	        		pstmt2.close();//关闭预编译对象2
	        	}
	        } catch (Exception e) {
	        	
	        	e.printStackTrace();
	        }
	        
	        try {
	            
	            if (conn!=null) {
	                conn.close();//关闭连接对象
	            }
	            
	        } catch (Exception e) {
	            
	            e.printStackTrace();
	        }
	}
	
	/**
	 * query data1
	 */
	/*public Map<String, List<UrlAndTemplateBean>> getData1(String querySql) {

		Map<String, List<UrlAndTemplateBean>> map = new HashMap<String, List<UrlAndTemplateBean>>();
		List<UrlAndTemplateBean> beans1 = new ArrayList<UrlAndTemplateBean>();
		List<UrlAndTemplateBean> beans2 = new ArrayList<UrlAndTemplateBean>();
		List<UrlAndTemplateBean> beans3 = new ArrayList<UrlAndTemplateBean>();
		String template_content_temp1 = "";
		String template_content_temp2 = "";
		String template_content_temp3 = "";

		try {
			pstm1 = conn.prepareStatement(querySql);
			rs1 = pstm1.executeQuery();
			while (rs1.next()) {
				Integer tId = rs1.getInt("tId");
				if (tId == 1) {
					template_content_temp1 = rs1.getString("template_content");
					UrlAndTemplateBean bean = new UrlAndTemplateBean();
					bean.setId(rs1.getInt("id"));
					bean.setSourceProvice(rs1.getString("province"));
					bean.setSourceCity(rs1.getString("city"));
					String baseUrl = rs1.getString("url").substring(0,
							rs1.getString("url").length() - 3);// 根据具体形式进行修改
					List<String> sourceUrlList = new ArrayList<String>();
					for (int i = 1; i <= rs1.getInt("page_num"); i++) {
						sourceUrlList.add(baseUrl + i);
					}
					bean.setSourceUrllList(sourceUrlList);
					beans1.add(bean);
					break;
				}
				if (tId == 2) {
					template_content_temp2 = rs1.getString("template_content");
					UrlAndTemplateBean bean = new UrlAndTemplateBean();
					bean.setId(rs1.getInt("id"));
					bean.setSourceProvice(rs1.getString("province"));
					bean.setSourceCity(rs1.getString("city"));
					String baseUrl = rs1.getString("url").substring(0,
							rs1.getString("url").length() - 3);// 根据具体形式进行修改
					List<String> sourceUrlList = new ArrayList<String>();
					for (int i = 1; i <= rs1.getInt("page_num"); i++) {
						sourceUrlList.add(baseUrl + i);
					}
					bean.setSourceUrllList(sourceUrlList);
					beans2.add(bean);
					break;
				}
				if (tId == 3) {
					template_content_temp3 = rs1.getString("template_content");
					UrlAndTemplateBean bean = new UrlAndTemplateBean();
					bean.setId(rs1.getInt("id"));
					bean.setSourceProvice(rs1.getString("province"));
					bean.setSourceCity(rs1.getString("city"));
					String baseUrl = rs1.getString("url").substring(0,
							rs1.getString("url").length() - 3);// 根据具体形式进行修改
					List<String> sourceUrlList = new ArrayList<String>();
					for (int i = 1; i <= rs1.getInt("page_num"); i++) {
						sourceUrlList.add(baseUrl + i);
					}
					bean.setSourceUrllList(sourceUrlList);
					beans3.add(bean);
					break;
				}
			}
			map.put(template_content_temp1, beans1);
			map.put(template_content_temp2, beans2);
			map.put(template_content_temp3, beans3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}*/
}
