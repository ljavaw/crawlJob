package com.dinfo.crawl;


import java.util.Map;
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
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.dinfo.crawl.conf.JobConfig;
import com.dinfo.crawl.job.CrawlJob;

public class TaskCrawl {
	
	public void crawlStart(Map<String,JobConfig> configMap){
		 SchedulerFactory sf = new StdSchedulerFactory();
		 Scheduler sched = null ;
		try {
			sched = sf.getScheduler();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		Set<Entry<String, JobConfig>> conEntrys = configMap.entrySet();
		for(Entry<String,JobConfig> confEntry:conEntrys){
			String jobName = confEntry.getKey();
			JobConfig jobConf = confEntry.getValue();
			String cronExp = jobConf.getCronExp();
			 JobDetail crawlJob = JobBuilder.newJob(CrawlJob.class).withIdentity(
					 jobName, "DefaultJobGroup").build();
			CronTrigger cron = TriggerBuilder.newTrigger().withIdentity(jobName,jobName+"Group")
			                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build();
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
