package com.example.demo.webRtc.test.concurrent;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTask extends RecursiveTask<Long>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long MAX = 150000000000L;
	private static final long THRESHOLD = 15000L;
	private long start;
	private long end;

	public ForkJoinTask(long start, long end)
	{
		this.start = start;
		this.end = end;
	}

	public static void main(String[] args)
	{
		//test();
		System.out.println("--------------------");
		testForkJoin();
	}

	private static void test()
	{
		System.out.println("test");
		long start = System.currentTimeMillis();
		Long sum = 0L;
		for (long i = 0L; i <= MAX; i++)
		{
			sum += i;
		}
		System.out.println(sum);
		System.out.println(System.currentTimeMillis() - start + "ms");
	}

	private static void testForkJoin()
	{
		System.out.println("testForkJoin");
		Instant start = Instant.now();
		ForkJoinPool forkJoinPool = new ForkJoinPool(5);
		Long sum = forkJoinPool.invoke(new ForkJoinTask(1, MAX));
		System.out.println(sum);
		System.out.println(Duration.between(Instant.now(),start).toMinutes());
	}

	@Override
	protected Long compute()
	{
		System.out.println(Thread.currentThread().getId());
		long sum = 0;
		if (end - start <= THRESHOLD)
		{
			for (long i = start; i <= end; i++)
			{
				sum += i;
			}
			return sum;
		} else
		{
			long mid = (start + end) / 2;

			ForkJoinTask task1 = new ForkJoinTask(start, mid);
			task1.fork();

			ForkJoinTask task2 = new ForkJoinTask(mid + 1, end);
			task2.fork();

			return task1.join() + task2.join();
		}
	}

}