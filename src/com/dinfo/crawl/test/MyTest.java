package com.dinfo.crawl.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.dinfo.mail.SendMailTask;

/**
 * 解析xml模板并生成新的xml文件
 * @author ljavaw
 */
public class MyTest {
	 //时间间隔(一天)  
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;  
    //crawl.properties中的配置
    public static Map<String, String> CRAWL_PROPERTIES = null;
    
	public void mainMethod(Map<String, String> configMap) {
    	
    	MyTest test = new MyTest();
    	CRAWL_PROPERTIES = configMap;
    	String[] t1Array = CRAWL_PROPERTIES.get("timeTask1").split(",");
    	String[] t2Array = CRAWL_PROPERTIES.get("timeTask2").split(",");
    	String[] t3Array = CRAWL_PROPERTIES.get("timeTask3").split(",");
    	String[] timerSendMail = CRAWL_PROPERTIES.get("timerSendMail").split(",");

        Task1 task1 = new Task1();
		test.timerManager(Integer.parseInt(t1Array[0]),Integer.parseInt(t1Array[1]),Integer.parseInt(t1Array[2]),task1);
		Task2 task2 = new Task2();
		test.timerManager(Integer.parseInt(t2Array[0]),Integer.parseInt(t2Array[1]),Integer.parseInt(t2Array[2]),task2);
		Task3 task3 = new Task3();
		test.timerManager(Integer.parseInt(t3Array[0]),Integer.parseInt(t3Array[1]),Integer.parseInt(t3Array[2]),task3);
		SendMailTask sendMailTask = new SendMailTask();
		test.timerManager(Integer.parseInt(timerSendMail[0]),Integer.parseInt(timerSendMail[1]),Integer.parseInt(timerSendMail[2]),sendMailTask);
		
	}
	
	public static void main(String[] args) {
    	
    	MyTest test = new MyTest();
    	CRAWL_PROPERTIES = parseProperties();
    	String[] t1Array = CRAWL_PROPERTIES.get("timeTask1").split(",");
    	String[] t2Array = CRAWL_PROPERTIES.get("timeTask2").split(",");
    	String[] t3Array = CRAWL_PROPERTIES.get("timeTask3").split(",");
    	String[] timerSendMail = CRAWL_PROPERTIES.get("timerSendMail").split(",");

    	Task1 task1 = new Task1();
		test.timerManager(Integer.parseInt(t1Array[0]),Integer.parseInt(t1Array[1]),Integer.parseInt(t1Array[2]),task1);
		Task2 task2 = new Task2();
		test.timerManager(Integer.parseInt(t2Array[0]),Integer.parseInt(t2Array[1]),Integer.parseInt(t2Array[2]),task2);
		Task3 task3 = new Task3();
		test.timerManager(Integer.parseInt(t3Array[0]),Integer.parseInt(t3Array[1]),Integer.parseInt(t3Array[2]),task3);
		SendMailTask sendMailTask = new SendMailTask();
		test.timerManager(Integer.parseInt(timerSendMail[0]),Integer.parseInt(timerSendMail[1]),Integer.parseInt(timerSendMail[2]),sendMailTask);
	}
	/**
	 * 定时器
	 * @param hour
	 * @param minute
	 * @param second
	 * @param task
	 */
	public void timerManager(int hour, int minute, int second, TimerTask task) {
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		
		Date date = calendar.getTime();
		if(date.before(new Date())){
			date = this.changeDay(date, 1);//增加一天
		}
		
		Timer timer = new Timer();
		//安排指定的任务在指定的时间开始进行重复的固定延迟执行。 
		timer.schedule(task,  new Date(), PERIOD_DAY);
		
	}
	/**
	 * 增加或减少天数
	 * @param date
	 * @param num
	 * @return
	 */
	public Date changeDay(Date date, int num){
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, num);
		
		return calendar.getTime();
	}
	
	/**
	 * 解析.properties 文件
	 * @param platformType
	 * @return
	 */
	public static Map<String, String> parseProperties() {
		
		Map<String, String> map = new HashMap<String, String>();
		FileInputStream fis = null;  
        try {  
            File file = new File("crawl.properties");  
			fis = new FileInputStream(file);
            Properties param = new Properties();  
            param.load(fis);  
            map.put("driverclass", param.getProperty("driverclass"));
            map.put("dburl", param.getProperty("dburl"));
            map.put("username", param.getProperty("username"));
            map.put("password", param.getProperty("password"));
            map.put("timeTask1", param.getProperty("timeTask1"));
            map.put("timeTask2", param.getProperty("timeTask2"));
            map.put("timeTask3", param.getProperty("timeTask3"));
            map.put("templateXmlTimeTask1", param.getProperty("templateXmlTimeTask1"));
            map.put("templateXmlTimeTask2", param.getProperty("templateXmlTimeTask2"));
            map.put("templateXmlTimeTask3", param.getProperty("templateXmlTimeTask3"));
            map.put("timeInterval", param.getProperty("timeInterval"));
            map.put("timerSendMail", param.getProperty("timerSendMail"));
            map.put("spaceTimerSendMail", param.getProperty("spaceTimerSendMail"));
            map.put("hadoop.home.dir", param.getProperty("hadoop.home.dir"));
            map.put("hbase.rootdir", param.getProperty("hbase.rootdir"));
            map.put("hbase.zookeeper.quorum", param.getProperty("hbase.zookeeper.quorum"));
        } catch (Exception e) {
			e.printStackTrace();
		} finally {  
            try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }  
        return map;
	}
	
}
