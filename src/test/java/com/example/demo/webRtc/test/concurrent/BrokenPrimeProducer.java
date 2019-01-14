package com.example.demo.webRtc.test.concurrent;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

class BrokenPrimeProducer extends Thread
{
	private volatile boolean cancelled = false;
	
	private final BlockingQueue<BigInteger> queue;
	
	public BrokenPrimeProducer(BlockingQueue<BigInteger> queue)
	{
		this.queue = queue;
	}

	@Override
	public void run()
	{
		try
		{
			BigInteger i = BigInteger.ONE;
			while(!cancelled) {
				queue.put(i.nextProbablePrime());
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void cancell() {
		cancelled = true;
	}
	
	
	

}
