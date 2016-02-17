package com.dinfo.crawl.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement  
public class Job implements Cloneable,Serializable{
	
	@XmlElement(name="name")
	private String jobName;
	@XmlElement(name="cron")
	private String jobCron;
	@XmlElement(name="jobtype")
	private String jobtype;
	@XmlElement(name="source-save-type")
	private String sourceSaveType;
	@XmlElement(name="source-path")
	private String sourcePath;
	@XmlElements(value = {@XmlElement(type=Page.class, name="page")})
	private List<Page> pageList;
	
	
	//浅克隆
	@SuppressWarnings("finally")
	public Object clone() {   
		 Job job = null;
		 try{
			 job = (Job)super.clone();
          }catch(CloneNotSupportedException e){
              e.printStackTrace();
          }finally{
	              return job;
	       }  
	 }  
	
	//深克隆（利用串行化深克隆一个对象，把对象以及它的引用读到流里，在写入其他的对象）
	public Object deepClone() throws IOException,ClassNotFoundException {
        //将对象写到流里
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        //从流里读回来
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
	
	
	public String getJobName() {
		return jobName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobCron() {
		return jobCron;
	}

	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}
	public List<Page> getPageList() {
		return pageList;
	}

	public void setPageList(List<Page> pageList) {
		this.pageList = pageList;
	}

	public String getJobtype() {
		return jobtype;
	}

	public void setJobtype(String jobtype) {
		this.jobtype = jobtype;
	}

	public String getSourceSaveType() {
		return sourceSaveType;
	}

	public void setSourceSaveType(String sourceSaveType) {
		this.sourceSaveType = sourceSaveType;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
}
