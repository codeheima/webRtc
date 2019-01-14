package com.example.demo.webRtc.test.concurrent.entity;

public class ThreadLocalEntity
{
	private String name;
	
	private long threadId;
	
	private long expired;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getThreadId()
	{
		return threadId;
	}

	public void setThreadId(long threadId)
	{
		this.threadId = threadId;
	}

	public long getExpired()
	{
		return expired;
	}

	public void setExpired(long expired)
	{
		this.expired = expired;
	}
	
	

}
