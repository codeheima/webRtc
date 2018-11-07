package com.example.demo.webRtc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestDemo
{
	private static final ExecutorService service = new ThreadPoolExecutor(15, 20, 0, 
			TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	
	private final static File pushF = new File("push.txt");
	
	private ThreadLocal<Long> count = new ThreadLocal<>();
	
	
	public static void main(String[] args) throws InterruptedException
	{
		while(true) {
			service.execute(()->{
				TestDemo t = new TestDemo();
				t.count.set(Thread.currentThread().getId());
				FileWriter fw = null;
				try
				{
					fw = new FileWriter(pushF, true);
					fw.write(UUID.randomUUID().toString()+"\n");
					fw.write((int) t.count.get().longValue()+"\n");
					fw.flush();
					fw.close();
				} catch (IOException e)
				{
					
					e.printStackTrace();
				}finally {
					if(fw!=null)
						try
						{
							fw.close();
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
			});
			Thread.sleep(3000);
			
		}
	
		
	}
	
	

}
