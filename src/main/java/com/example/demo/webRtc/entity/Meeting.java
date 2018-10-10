package com.example.demo.webRtc.entity;

import java.io.Serializable;
import java.util.Date;

public class Meeting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String hostName;
	private String subject;
	private Integer maxmembers;
	private Date preStartTime;
	private Date preEndTime;
	private Date startTime;
	private Date endTime;
	private Date createAt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getMaxmembers() {
		return maxmembers;
	}
	public void setMaxmembers(Integer maxmembers) {
		this.maxmembers = maxmembers;
	}
	public Date getPreStartTime() {
		return preStartTime;
	}
	public void setPreStartTime(Date preStartTime) {
		this.preStartTime = preStartTime;
	}
	public Date getPreEndTime() {
		return preEndTime;
	}
	public void setPreEndTime(Date preEndTime) {
		this.preEndTime = preEndTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	
	

}
