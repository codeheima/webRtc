package com.example.demo.webRtc.test.concurrent;

import com.example.demo.webRtc.test.concurrent.entity.ThreadLocalEntity;

public class ThreadLocalTest
{
	private ThreadLocal<ThreadLocalEntity> te = new ThreadLocal<>();;

	public void test()
	{
		ThreadLocalEntity e = new ThreadLocalEntity();
		e.setExpired(System.currentTimeMillis() + 3600 * 1000l);
		e.setName(Thread.currentThread().getName());
		e.setThreadId(Thread.currentThread().getId());
		te.set(e);
		te.get();

	}

}
