package com.dinfo.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TimerTask;

import com.dinfo.crawl.main.HandleDatabase;
import com.dinfo.crawl.main.MainTask;

public class SendMailTask extends TimerTask{

	//上次的数据的条数
	public static int FIRST_COUNT = 0;
	public String tableName = "community_info_t";
	
	Connection conn = HandleDatabase.getConnection();
	PreparedStatement pstmt1;
	PreparedStatement pstmt2;
	ResultSet rs1;
	ResultSet rs2;
	
	HandleDatabase handle = new HandleDatabase(conn, pstmt1, pstmt2, rs1, rs2);
	SendMail sendMail = new SendMail();
	
	@Override
	public void run() {
		FIRST_COUNT = getCount();//第一次查询数据条数
		while (true) {
			try {
				Thread.sleep(Integer.parseInt(MainTask.CRAWL_PROPERTIES.get("spaceTimerSendMail")));
				int temp = getCount();//第二次查询数据条数
				if(temp <= FIRST_COUNT){
					sendMail.sendMailReport(tableName+"中的数据未增加！");
					System.out.println("已发送数据报告邮件！");
				}
				FIRST_COUNT = temp;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getCount() {
		
		String sql = "SELECT * FROM "+tableName;
		return handle.getCountData(sql);
	}
}
