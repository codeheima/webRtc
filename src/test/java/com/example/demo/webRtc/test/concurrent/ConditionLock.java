package com.example.demo.webRtc.test.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionLock
{
	final Lock lock = new ReentrantLock();// 锁对象
	final Condition full = lock.newCondition();// 写线程条件
	final Condition empty = lock.newCondition();// 读线程条件

	private static List<String> cache = new LinkedList<String>();

	// 生产者线程任务
	public void put(String str)
			throws InterruptedException
	{
		// 获取线程锁
		lock.lock();
		try
		{
			System.out.println(String.format("生产者%s;cacheSize%d", Thread.currentThread().getName(), cache.size()));
			while (cache.size() >= 100)
			{
				System.out.println("超出缓存容量.暂停写入.");
				// 生产者线程阻塞
				full.await();
				System.out.println("生产者线程阻塞");
			}
			System.out.println("写入数据");
			cache.add(str);
			// 唤醒消费者
			empty.signalAll();
			System.out.println("唤醒消费者");
		} finally
		{
			// 锁使用完毕后不要忘记释放
			lock.unlock();
		}
	}

	// 消费者线程任务
	public void get()
			throws InterruptedException
	{
		try
		{
			while (!Thread.interrupted())
			{
				// 获取锁
				lock.lock();
				System.out.println(String.format("消费者%s;cacheSize%d", Thread.currentThread().getName(), cache.size()));
				while (cache.size() == 0)
				{
					System.out.println("缓存数据读取完毕.暂停读取");
					// 消费者线程阻塞
					empty.await();
					System.out.println("消费者线程阻塞");
				}
				System.out.println("读取数据");
				cache.remove(cache.size() - 1);
				// 唤醒生产者线程
				full.signalAll();
				System.out.println("唤醒生产者线程");
			}

		} finally
		{
			// 锁使用完毕后不要忘记释放
			lock.unlock();
		}
	}

	public static void main(String[] args)
	{
		ConditionLock demo = new ConditionLock();
		for (int i = 0; i < 10; i++)
		{
			new Thread(() ->
			{
				try
				{
					while (true)
						demo.put(UUID.randomUUID().toString());
				} catch (InterruptedException e)
				{
					Thread.interrupted();
					e.printStackTrace();
				}
			}).start();

		}
		new Thread(() ->
		{
			try
			{
				while (true)
					demo.get();
			} catch (InterruptedException e)
			{
				Thread.interrupted();
				e.printStackTrace();
			}
		}).start();

	}
}