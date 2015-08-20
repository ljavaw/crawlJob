package com.dinfo.hbase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** brief description
 * <p>Date : 2015年6月3日 上午11:34:38</p>
 * <p>Module : </p>
 * <p>Description: </p>
 * <p>Remark : </p>
 * @author Administrator
 * @version 
 * <p>------------------------------------------------------------</p>
 * <p> 修改历史</p>
 * <p> 序号 日期 修改人 修改原因</p>
 * <p> 1 </p>
 */

public class HbaseUtil {

	/**
	 * 得到指定时间戳
	 * @return
	 */
	public static long getAssignTime(String assignTime){
		
		long timestamp = 0;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = df.parse(assignTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			timestamp = cal.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	public static String md5(String plainText) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(
					plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}
}
