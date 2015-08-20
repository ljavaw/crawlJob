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
public class Crawl implements Cloneable, Serializable {

	@XmlElements(value = { @XmlElement(type = Job.class, name = "job") })
	private List<Job> jobList;

	// 浅克隆
	@SuppressWarnings("finally")
	public Object clone() {
		Job job = null;
		try {
			job = (Job) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} finally {
			return job;
		}
	}

	// 深克隆（利用串行化深克隆一个对象，把对象以及它的引用读到流里，在写入其他的对象）
	public Object deepClone() throws IOException, ClassNotFoundException {
		// 将对象写到流里
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		// 从流里读回来
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		return ois.readObject();
	}

	public List<Job> getJobList() {
		return jobList;
	}

	public void setJobList(List<Job> jobList) {
		this.jobList = jobList;
	}

}
