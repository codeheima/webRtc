package com.example.demo.webRtc.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class HttpConnectionUtils
{
	
	//private final static ExecutorService exe = Executors.newFixedThreadPool(800);
	
	private final static File timefile = new File("time.txt");
	
	private final static Gson gson = new GsonBuilder().create();
	public static <K,V> String request(String path, Map<K, V> map)
			throws Exception
	{
		HttpURLConnection conn = null;
		try
		{
			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpsURLConnection)
			{
				HttpsURLConnection httpsConn = (HttpsURLConnection) conn;

				// 信用所有
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
				{
					public X509Certificate[] getAcceptedIssuers()
					{
						return new X509Certificate[] {};
					}

					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException
					{
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException
					{
					}
				} };
				SSLContext sc = SSLContext.getInstance("SSL");// TLS
				sc.init(null, trustAllCerts, new SecureRandom());
				SSLSocketFactory newFactory = sc.getSocketFactory();
				httpsConn.setSSLSocketFactory(newFactory);
				// 不验证主机
				httpsConn.setHostnameVerifier(new HostnameVerifier()
				{
					public boolean verify(String hostname, SSLSession session)
					{
						return true;
					}
				});
			}
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			conn.setRequestProperty("Oc_Chatserver_MinVersion", "3.0.0");
			conn.setRequestProperty("Oc_Platform_AppVersion", "2.8.1");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.getOutputStream().write(deal(map).getBytes("UTF-8"));
			String rspStr = IOUtils.toString(conn.getInputStream(), "UTF-8");
			return rspStr;
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
				conn = null;
			}
		}
	}

	private static <K,V> String deal(Map<K, V> map)
	{
		StringBuilder sb = new StringBuilder();
		
		map.forEach((K k,V v)->{
			//if(!StringUtils.isEmpty((String)v)) {
				sb.append(k);
				sb.append("=");
				sb.append(v);
				sb.append("&");
			//}
				
		});
		return sb.toString().endsWith("&")? sb.toString().substring(0,sb.toString().length()-1):sb.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		FileWriter fw = new FileWriter(timefile,true);
		Map<String, Object> map = new LinkedHashMap<>();
//		map.put("xiaohuifrom", "qwer");
//		map.put("xiaohuiIds", "qwerty");
//		map.put("roomName", "222806b8663549e2b17c4225d3e2b");
//		map.put("chatserver_id", "2");
//		map.put("company_name", "亦云小慧");
	//map.put("im_user_name", "277bd2b6d6784");//		map.put("message", "asfdsgfsdgf");		//map.put("meeting_id", "108");
//		map.put("type", "file");
		//map.put("im_user_name", "88d149");
		//map.put("token", "e3b74e7708ef44b5888f560db779e69f95aa7549");
		map.put("zz", "safsaf");
		map.put("zx", "asfsdf");
		//map.put("im_user_name", "aa96d0a8927a4b");
		//map.put("chatserver_id", "1");//    13603bdecce747c98f0f36e29c231df05a362fa9
		//map.put("thirdparty_id", "3");
		//map.put("data", "h/nT8mXYKT5jpiu0QHWLaeVRIjY4zceFRISdbC0x7hq+OXqQLkf1r2zQc3RAOcef");
		//map.put("im_room_names", "78464567ca774ee89807f,51e8da");//,173791b6e14540b9aff0,f3d5845c51054cdaba1eed4,ba60f5e3737042a,0ac27ed8678c4fe38bcc236a38a3bf0,1c77828113e24f6f81aefc,78464567ca774ee89807f,ba8ea6f1f4d7490889c5598435cfea
//		Map<String, String> map1 = new LinkedHashMap<>();
//		map.put("account", "13679487149");
//		map.put("password", "123456");
//		map.put("platform_type", "4");
		//192.168.105.27:7100;172.28.98.50:7100
		//String path = "http://192.168.1.106:7110/mock/thirdparty/chat/room/do_send";
		//String path = "https://192.168.1.106:7143/business/user/get";
		//String path = "http://192.168.1.106:8110/hottub/user/group/list";
		//String path = "http://appcc.onecloud.cn/hottub/push/send";
		//String path = "http://chatoc.cloudak47.com:7101/business/user/login1";
		String path = "http://192.168.1.113:7110/chat//file/deal";
		//String path4 = "http://chat.cloudak47.com:7100/business/user/thirdparty/encrypt/show";
	//	System.out.println(request(path, map));
//		String path1 = "http://172.28.98.50:7100/business/user/relate1";
	//	while(true) {
//			Future<String> submit = exe.submit(new Callable<String>()
//			{
//
//				@Override
//				public String call()
//						throws Exception
//				{
//					
//					return request(path, map);
//				}
//			});
//			exe.execute(()->{
//				try
//				{
//					System.out.println(String.format("thread:%dstart\n", Thread.currentThread().getId()));
					//map.put("content", UUID.randomUUID());
		while(true) {
					long start = System.currentTimeMillis();
		
					//String response = request(path, map);
					String response = uploadFile(path, map ,"0.png");
					long total = System.currentTimeMillis()-start;
//					JsonObject json = gson.fromJson(response, JsonObject.class);
//					Long openfire_time = json.get("openfireTime").getAsLong();
//					Long chatserver_total = json.get("chatserverTime").getAsLong();
//					Long chatserver_time = chatserver_total-openfire_time;
//					Long clientIoTime = total-chatserver_total;
//					fw.write(openfire_time+"\t"+chatserver_time+"\t"+chatserver_total+"\t"+clientIoTime+"\t"+total+"\n");
//					fw.flush();
					System.out.println(response);
					System.out.println(total);
					
					Thread.sleep(30000);
		}
//					java.lang.Thread.sleep(500);
//				} catch (Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			});String
//		}
		
	}
	
	 /**
     * 多文件上传的方法
     * 
     * @param actionUrl：上传的路径
     * @param uploadFilePaths：需要上传的文件路径，数组
     * @return
     */
    @SuppressWarnings("finally")//1534815632000
	public static String uploadFile(String actionUrl, Map<String, Object> map,String... uploadFilePaths) {
        String end = "";
        String twoHyphens = "";
        String boundary = "*****";

        DataOutputStream ds = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            // 统一资源
            URL url = new URL(actionUrl);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // 设置是否向httpUrlConnection输出
            httpURLConnection.setDoOutput(true);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("POST");
            // 设置字符编码连接参数
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 设置请求内容类型
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // 设置DataOutputStream
            ds = new DataOutputStream(httpURLConnection.getOutputStream());
            ds.writeBytes(getStrParams(map));
            for (int i = 0; i < uploadFilePaths.length; i++) {
                String uploadFile = uploadFilePaths[i];
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + filename
                        + "\"" + end);
                ds.writeBytes(end);
                FileInputStream fStream = new FileInputStream(uploadFile);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(end);
                /* close streams */
                fStream.close();
            }
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            ds.flush();
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                tempLine = null;
                resultBuffer = new StringBuffer();
                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return resultBuffer.toString();
        }
    }
    
    private static String getStrParams(Map<String,Object> strParams){
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, Object> entry : strParams.entrySet() ){
            strSb.append("--")
                    .append("*****")
                    .append("\r\n")
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + "\r\n")
                    .append("Content-Type: text/plain; charset=utf-8" + "\r\n")
                    .append("Content-Transfer-Encoding: 8bit" + "\r\n")
                    .append("\r\n")// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append("\r\n");
        }
        return strSb.toString();
    }}
