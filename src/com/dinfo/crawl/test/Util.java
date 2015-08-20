package com.dinfo.crawl.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.dinfo.crawl.bean.SourceExcelBean;
/**
 * @author ljavaw
 *
 */
public class Util {

	/**
	 * xml To String
	 */
	public static String xmlToString(String pathName) {
		String result = "";
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(pathName));
			result = document.asXML();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * String To xml
	 */
	public static void stringToXml(String xmlString, String pathName) {
		try {
			FileWriter fWriter = new FileWriter(pathName);
			Document document = DocumentHelper.parseText(xmlString);
			XMLWriter writer = new XMLWriter(fWriter);// 输出路径
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自动加30分钟
	 * 
	 * @param minute
	 * @param hour
	 * @return
	 */
	public static String getCorn(String second, String minute, String hour, int jobNum) {
		//自动加30分钟(得到的是毫秒数  需要换算成分钟)
		int timeInterval = Integer.parseInt(MyTest.CRAWL_PROPERTIES.get("timeInterval"));
		int addNum = jobNum - 1;//第一个job不需要增加时间
		Integer minuteInt = 0;
		Integer hourInt = 0;
		if (minute != null && !minute.equals("")) {
			minuteInt = Integer.parseInt(minute);
		}
		if (hour != null && !hour.equals("")) {
			hourInt = Integer.parseInt(hour);
		}
		if (minuteInt + timeInterval/60000 * addNum < 60) {
			minuteInt = minuteInt + timeInterval/60000 * addNum;
		}else{
			hourInt = hourInt + (minuteInt + timeInterval/60000 * addNum) / 60;
			hourInt = (hourInt >= 24 ? hourInt % 24 : hourInt);
			minuteInt = (minuteInt + timeInterval/60000 * addNum) % 60;
		}
		return second+" "+minuteInt.toString()+" "+hourInt.toString()+" * * ?";
	}
	/**
	 * 自动加30分钟
	 * 
	 * @param minute
	 * @param hour
	 * @return
	 */
	public static String getCorn1(String second, String minute, String hour, 
			String day, String mouth, String week, String year, int jobNum) {
		if(year != null && !year.equals("") 
				&& mouth != null && !mouth.equals("") 
				&& day != null && !day.equals("") 
				&& week != null && !week.equals("")){
			int timeInterval = Integer.parseInt(MyTest.CRAWL_PROPERTIES.get("timeInterval"));//自动加30分钟
			int addNum = jobNum - 1;//第一个job不需要增加时间
			if(year.equals("*")){//增量的
				String dateStr = "2015"+"02"+day+hour+minute+second;
				long millisecond = Util.dateToMillisecond(dateStr);
				millisecond = millisecond+addNum*timeInterval;
				String resultDateStr = Util.millisecondToDate(millisecond);
				String dayTemp = resultDateStr.substring(6,8);
				if(dayTemp != null && !("").equals(dayTemp) && Integer.valueOf(dayTemp) > 28){
					dayTemp = "28";
				}
				//格式：秒   分   小时   日期   月份  星期   年(秒   分   小时 只去了一个0)
				return resultDateStr.substring(13)+" "+resultDateStr.substring(11,12)+" "+resultDateStr.substring(9,10)+
						" "+dayTemp+" "+mouth.replace("|", ",")+" "+week+" "+year;
			}else{//全量的
				String dateStr = year+mouth+day+hour+minute+second;
				long millisecond = Util.dateToMillisecond(dateStr);
				millisecond = millisecond+addNum*timeInterval;
				String resultDateStr = Util.millisecondToDate(millisecond);
				//System.out.println(resultDateStr.substring(12));
				//格式：秒   分   小时   日期   月份  星期   年
				return resultDateStr.substring(12)+" "+resultDateStr.substring(10,12)+" "+resultDateStr.substring(8,10)+
						" "+resultDateStr.substring(6,8)+" "+resultDateStr.substring(4,6)+" "+week+" "+resultDateStr.substring(0,4);
			}
		}else{
			return Util.getCorn(second, minute, hour, jobNum);
		}
	}
	/**
	 * 日期转毫秒
	 */
	public static long dateToMillisecond(String date){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		long millisecond = 0;
		try {
			millisecond = sdf.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return millisecond;
	}
	/**
	 * 毫秒转日期
	 */
	public static String millisecondToDate(long millisecond){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millisecond);
		Date date = c.getTime();
		return sdf.format(date);
	}

	/**
	 * 获得全量采集日期
	 */
	public static String getCrawlDate(){
		String result = "";
		Date date = new Date();
		long millisecond = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd,?,HH,mm,ss");
		Calendar c = Calendar.getInstance();
		//当前时间向后推迟一小时后执行采集
		c.setTimeInMillis(millisecond + 3600000);
		result = sdf.format(c.getTime());
		return result;
	}
	
	/**
	 * 解析excel
	 */
	public static List<SourceExcelBean> parserExcel(String filePath) {

		List<SourceExcelBean> sebList = new ArrayList<SourceExcelBean>();
		try {
			File file = new File(filePath); // 创建文件对象
			Workbook wb = Workbook.getWorkbook(file); // 从文件流中获取Excel工作区对象（WorkBook）
			Sheet[] sheets = wb.getSheets(); // 从工作区中取得页（Sheet）
			
			for(int z=0; z<sheets.length; z++){
				Sheet sheet = sheets[z];
				int rowNum = getRightRows(sheet);
				/*System.out.println(rowNum);
				System.out.println(sheet.getColumns());*/
				for (int i = 1; i < rowNum; i++) { // 循环打印Excel表中的内容
					SourceExcelBean bean = new SourceExcelBean();
					for (int j = 0; j < sheet.getColumns(); j++) {
						Cell cell = sheet.getCell(j, i);// (列数，行数)
						if (cell.getContents() != null
								&& !cell.getContents().equals("")) {
							switch (j) {
							case 0:
								bean.setProvinceExcel(cell.getContents());
								break;
							case 1:
								bean.setCityExcel(cell.getContents());
								break;
							case 2:
								bean.setAreaExcel(cell.getContents());
								break;
							case 3:
								bean.setUrlExcel(cell.getContents());
								break;
							case 4:
								bean.setAllPageExcel(cell.getContents());
								break;
							case 5:
								bean.setTemplateIdExcel(Integer.parseInt(cell
										.getContents()));
								break;
							case 6:
								bean.setPlatformTypeIdExcel(changeType(cell
										.getContents()));
								break;
							default:
								break;
							}
							//System.out.println(cell.getContents());
						}
					}
					sebList.add(bean);
				}
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sebList;
	}

	
	
	
	public static Integer changeType(String platformType) {
		// 平台类型: 0:搜房 1:安居客 2:大众点评 3:特惠商户 4:企业
		if(platformType != null && !platformType.equals("")){
			switch (platformType.trim()) {
			case "搜房":
				return 0;
			case "安居客":
				return 1;
			case "大众点评":
				return 2;
			case "特惠商户":
				return 3;
			case "企业":
				return 4;
			default:
				return null;
			}
		}else{
			return null;
		}
	}

	/**
	 * 返回去掉空行的记录数
	 * @param sheet
	 * @return
	 */
	private static int getRightRows(Sheet sheet) {

		int rsCols = sheet.getColumns(); // 列数
		int rsRows = sheet.getRows(); // 行数
		int nullCellNum;
		int afterRows = rsRows;

		for (int i = 1; i < rsRows; i++) { // 统计行中为空的单元格数
			nullCellNum = 0;
			for (int j = 0; j < rsCols; j++) {
				String val = sheet.getCell(j, i).getContents();
				val = StringUtils.trimToEmpty(val);
				if (StringUtils.isBlank(val))
					nullCellNum++;
			}
			if (nullCellNum >= rsCols) { // 如果nullCellNum大于或等于总的列数(即一整行都没有数据)
				afterRows--;// 行数减一
			}
		}
		return afterRows;
	}
	
	/**
	 * logger
	 */
	public static Logger sample(String className){
		Logger logger = null;
	        try {
				logger = Logger.getLogger(Class.forName(className));
				PropertyConfigurator.configure("log4j.properties");
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	        return logger;
	    }
}
