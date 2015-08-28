package com.dinfo.crawl.enterprise;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
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
	public PreparedStatement pstm1;
	public ResultSet rs1;
	
	public EnterpriseHandleDatabase() {
		super();
	}
	public EnterpriseHandleDatabase(Connection conn, PreparedStatement pstm1, ResultSet rs1) {
		super();
		this.conn = conn;
		this.pstm1 = pstm1;
		this.rs1 = rs1;
	}
	/**
	 * 查询template_info_t中的数据
	 */
	public List<UrlAndTemplateBean> getData1(String querySql) {
		
		List<UrlAndTemplateBean> beans = new ArrayList<UrlAndTemplateBean>();
		try {
			pstm1 = conn.prepareStatement(querySql);
			rs1 = pstm1.executeQuery();
			
			while(rs1.next()){
				UrlAndTemplateBean bean = new UrlAndTemplateBean();
				String templateName = rs1.getString("template_name");
				String template_content = rs1.getString("template_content");
				
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
	 * 查询enterpris_info_t中的数据
	 */
	public List<UrlAndTemplateBean> getData2(String querySql) {
		
		List<UrlAndTemplateBean> beans = new ArrayList<UrlAndTemplateBean>();
		try {
			pstm1 = conn.prepareStatement(querySql);
			rs1 = pstm1.executeQuery();
			
			while(rs1.next()){
				UrlAndTemplateBean bean = new UrlAndTemplateBean();
				String companyName = rs1.getString("companyName");
				String stockCode = rs1.getString("stockCode");
				
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
	 * 解析模板入库，向template_info_t表中添加数据
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
	 * 向config_enterprise_info_t表中添加数据
	 * 含有blob字段类型
	 */
	@SuppressWarnings("rawtypes")
	public void addConfigInfo(Map<String, Map<String, JobConfig>> addConfigMap) {
		
		try {
			String insertSql = "INSERT INTO config_enterprise_info_t (name, config, url_xml)VALUES(?,?,?)";
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
