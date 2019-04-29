package com.example.demo.webRtc.util;

import java.io.IOException;

public class ImageUtil
{
	private static int ss = 0;
	public static void main(String[] args) throws IOException
	{
//		BufferedImage read = ImageIO.read(new File("timg.jpeg"));
//		ImageIO.write(read, "png", new File("0.png"));
		doss s = new ImageUtil.doss();
		System.out.println(doss(s,0));
		
		
	}
	
	private static int doss(doss s,int count) {
		if(count<50) {
			ss+=1;
			doss(s, count+=1);
		}
			
		return ss;
		
	}
	
	public static class doss{
		public int num;
	}

}
