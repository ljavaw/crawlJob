package com.dinfo.crawl.enterprise;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dinfo.crawl.bean.UrlAndTemplateBean;
import com.dinfo.crawl.conf.JobConfig;
import com.dinfo.crawl.test.MyTest;
import com.dinfo.crawl.test.Util;
/**
 * @author ljavaw
 */
public class EnterpriseHandleDatabase {

	public Connection conn;
	public PreparedStatement pstm;
	public ResultSet rs;
	
	public EnterpriseHandleDatabase() {
		super();
	}
	public EnterpriseHandleDatabase(Connection conn, PreparedStatement pstm, ResultSet rs) {
		super();
		this.conn = conn;
		this.pstm = pstm;
		this.rs = rs;
	}
	/**
	 * 查询template_info_t中的数据
	 */
	public List<UrlAndTemplateBean> getData1(String querySql) {
		
		List<UrlAndTemplateBean> beans = new ArrayList<UrlAndTemplateBean>();
		try {
			pstm = conn.prepareStatement(querySql);
			rs = pstm.executeQuery();
			
			while(rs.next()){
				UrlAndTemplateBean bean = new UrlAndTemplateBean();
				String templateName = rs.getString("template_name");
				String template_content = rs.getString("template_content");
				
				bean.setTemplateName(templateName);
				bean.setTemplate_content_source(template_content);
				beans.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return beans;
	}
	/**
	 * 查询enterpris_info_t中的数据(生成任务用)
	 */
	public List<UrlAndTemplateBean> getData2(String querySql) {
		
		List<UrlAndTemplateBean> beans = new ArrayList<UrlAndTemplateBean>();
		try {
			pstm = conn.prepareStatement(querySql);
			rs = pstm.executeQuery();
			
			while(rs.next()){
				UrlAndTemplateBean bean = new UrlAndTemplateBean();
				String companyName = rs.getString("companyName");
				String stockCode = rs.getString("stockCode");
				
				bean.setCompanyName(companyName);
				bean.setStockCode(stockCode);
				beans.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return beans;
	}
	/**
	 * 查询enterpris_info_t中的数据(解析pdf文件用)
	 */
	public Map<String, String> getData3(String querySql) {
		
		Connection conn1 = EnterpriseHandleDatabase.getConnection();
		PreparedStatement pstm1 = null;
		ResultSet rs1 = null;
		
		Map<String, String> map = new HashMap<String, String>();
		try {
			pstm1 = conn1.prepareStatement(querySql);
			rs1 = pstm1.executeQuery();
			while(rs1.next()){
				String companyAbbreviation = rs1.getString("companyAbbreviation");//公司简称
				String companyName = rs1.getString("companyName");//公司名称
				
				map.put("companyAbbreviation", companyAbbreviation);
				map.put("companyName", companyName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			closeDataSource(rs1, null, pstm1, null, conn1);
		}
		return map;
	}
	/**
	 * 解析模板入库，向template_info_t表中添加数据
	 */
	@SuppressWarnings("static-access")
	public void addInfo(String template_name, String file_path, int platform_type) {
		Util util = new Util();
		String insertSql = "INSERT INTO template_info_t (template_name, template_content, file_path, platform_type)VALUES(?,?,?,?)";
		String template_content = util.xmlToString(file_path);
		try {
			pstm = conn.prepareStatement(insertSql);
			pstm.setString(1, template_name);
			pstm.setString(2, template_content);
			pstm.setString(3, file_path);
			pstm.setInt(4, platform_type);
			pstm.execute();
			System.out.println("insert template_info_t the data successfully！");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 向config_enterprise_info_t表中添加数据
	 * 含有blob字段类型
	 */
	@SuppressWarnings("rawtypes")
	public void addConfigInfo(Map<String, Map<String, JobConfig>> addConfigMap) {
		
		try {
			String insertSql = "INSERT INTO config_enterprise_info_t (name, config, url_xml)VALUES(?,?,?)";
			pstm = conn.prepareStatement(insertSql);
			conn.setAutoCommit(false);
			Iterator it = addConfigMap.keySet().iterator();
			//添加到数据库
			while (it.hasNext()) {
				String name = it.next().toString();
				Map<String, JobConfig> resultMap = addConfigMap.get(name);
				Iterator temp = resultMap.keySet().iterator();
				while (temp.hasNext()) {
					String tempName = temp.next().toString();
					pstm.setString(1, tempName);//job名
					pstm.setObject(2, resultMap.get(tempName));//job对象
					pstm.setString(3, name);//xml字符串（只含有一个job）
					pstm.addBatch();
				}
			}
			pstm.executeBatch();
			conn.commit();
			System.out.println("insert the config_enterprise data successfully！");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 得到数据库连接
	 */
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
}
