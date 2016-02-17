package com.dinfo.crawl;


import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.dinfo.crawl.conf.JobConfig;
import com.dinfo.crawl.job.CrawlJob;

public class TaskCrawl {
	
	 public static SchedulerFactory initJobSchedulerFactory(String name){
         Properties prop = new Properties();
         File file = new File(name);
         try {
         	FileInputStream fin = new FileInputStream(file);
             prop.load( fin );
         } catch (IOException ex) {
             System.out.println(ex.toString());
         }
         StdSchedulerFactory ssf = null;
			try {
				ssf = new StdSchedulerFactory( prop );
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
         return ssf;
	    }
	
	
	public void crawlStart(Map<String,JobConfig> configMap,int type){
		 SchedulerFactory sf = initJobSchedulerFactory("quartz.properties");
		 
		 SchedulerFactory sf2 = initJobSchedulerFactory("quartzqy.properties");
		 Scheduler sched = null ;
		try {
			if(type == 1){
				sched = sf.getScheduler();
			}else if(type == 2){
				sched = sf2.getScheduler();
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		Set<Entry<String, JobConfig>> conEntrys = configMap.entrySet();
		for(Entry<String,JobConfig> confEntry:conEntrys){
			String jobName = confEntry.getKey();
			System.out.println("--------------------"+jobName);
			
			JobConfig jobConf = confEntry.getValue();
			String cronExp = jobConf.getCronExp();
			 JobDetail crawlJob = JobBuilder.newJob(CrawlJob.class).withIdentity(
					 jobName, "DefaultJobGroup").build();
			CronTrigger cron = TriggerBuilder.newTrigger().withIdentity(jobName,jobName+"Group")
			                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build();
			
//			Trigger cron = newTrigger().withIdentity(jobName,jobName+"Group").startNow().withSchedule(
//					SimpleScheduleBuilder.simpleSchedule().repeatForever()
//		                 .withIntervalInHours(24)).startNow().build();
	        JobDataMap dataMap = crawlJob.getJobDataMap();
	    	dataMap.put("jobName", jobName);
	    	try {
				sched.scheduleJob(crawlJob, cron);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
}
