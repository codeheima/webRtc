package com.example.demo.webRtc.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;



//import net.bytebuddy.asm.Advice.This;

@ServerEndpoint(value = "/roomChat")
@Component
public class WebRtcWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    private static ReentrantLock lock = new ReentrantLock();
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebRtcWebSocket> webSocketSet = new CopyOnWriteArraySet<WebRtcWebSocket>();
    
    private static Map<String,CopyOnWriteArraySet<WebRtcWebSocket>> roomMap = new ConcurrentHashMap<>();
    
    private static Map<String, WebRtcWebSocket> socketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    private String key;
    
    private String roomName;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
    	//if()
        this.session = session;
        this.key=UUID.randomUUID().toString();
        webSocketSet.add(this); //加入set中
        socketMap.put(key, this);
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
        	/*JSONObject jo = new JSONObject();
        	jo.accumulate("eventName", "_peers");
        	JSONObject data = new JSONObject();
        	data.accumulate("you", this.key);
            sendMessage();*/
        } catch (Exception e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
    	socketMap.remove(key);
    	lock.lock();
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        CopyOnWriteArraySet<WebRtcWebSocket> wss = roomMap.get(this.roomName);
        sendRemove(roomMap.get(this.roomName));
        wss.remove(this);
        lock.unlock();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    private void sendRemove(CopyOnWriteArraySet<WebRtcWebSocket> wss)
	{
    	JSONObject jo = new JSONObject();
    	jo.put("eventName", "_remove_peer");
    	JSONObject data = new JSONObject();
    	data.put("socketId", this.key);
    	jo.put("data",data);
    	wss.forEach((a)->{
    		if(!a.key.equals(this.key))
				try
				{
					sendMessage(jo.toJSONString());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
    	});
    			
		
	}

	/**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @throws IOException */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("来自客户端的消息:" + message);
        JSONObject jo = JSONObject.parseObject(message);
        JSONObject data = jo.getJSONObject("data");
        String room = data.getString("room");
        if("_join".equals(jo.getString("eventName"))) {
        	if(org.springframework.util.StringUtils.isEmpty(room))
        		room = "default";
        	this.roomName = room;
        	//先给房间里其他用户发送通知
        	//{\"eventName\":\"_new_peer\",\"data\":{\"socketId\":\"1232-osadf-safd\"}}
        	CopyOnWriteArraySet<WebRtcWebSocket> wss= roomMap.get(room);
        	if(null!=wss&&!wss.isEmpty()) {
        		for(WebRtcWebSocket c :wss) {
            		sendNewPeer(c);
            		//c.sendMessage(message);
            	}
        	}
        	lock.lock();
        	if(!roomMap.containsKey(room)) {
    			CopyOnWriteArraySet<WebRtcWebSocket> roomSockets = new CopyOnWriteArraySet<>();
    			roomSockets.add(this);
    			roomMap.put(room, roomSockets);
    		}else
    			roomMap.get(room).add(this);	
        	lock.unlock();
        	sendPeers(room);
        		
        }else{
        	String toKey = data.getString("socketId");
        	WebRtcWebSocket toWs = socketMap.get(toKey);
        	data.put("socketId", this.key);
        	toWs.sendMessage(jo.toJSONString());
        	
        }

    }

    private void sendNewPeer(WebRtcWebSocket c) throws IOException
	{
    	JSONObject jo = new JSONObject();
    	jo.put("eventName", "_new_peer");
    	JSONObject data = new JSONObject();
    	data.put("socketId", this.key);
//    	List<String> connections = new LinkedList<>();
//    	for(WebRtcWebSocket ws :roomMap.get(roomName)) {
//    		connections.add(ws.key);
//    	}
//    	data.put("connections", connections.toArray(new String[connections.size()]));
    	jo.put("data",data);
    	c.sendMessage(jo.toJSONString());
		
	}

	private void sendPeers(String roomName) throws IOException {
    	JSONObject jo = new JSONObject();
    	jo.put("eventName", "_peers");
    	JSONObject data = new JSONObject();
    	data.put("you", this.key);
    	List<String> connections = new LinkedList<>();
    	for(WebRtcWebSocket ws :roomMap.get(roomName)) {
    		if(!ws.key.equals(this.key))
    			connections.add(ws.key);
    	}
    	data.put("connections", connections.toArray(new String[connections.size()]));
    	jo.put("data",data);
    	sendMessage(jo.toJSONString());
		
		
	}

	@OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        //this.session.getBasicRemote().sendText(message);
    	lock.lock();
    	System.out.println(message);
        this.session.getBasicRemote().sendText(message);
        lock.unlock();
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        for (WebRtcWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebRtcWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebRtcWebSocket.onlineCount--;
    }

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getRoomName()
	{
		return roomName;
	}

	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}
    
    
}