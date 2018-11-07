package com.example.demo.webRtc.entity;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kurento.client.MediaPipeline;
import org.springframework.web.socket.WebSocketSession;

public class Room implements Closeable
{
	private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;
	private final String name;

	@Override
	public void close()
			throws IOException
	{

	}

	public Room(String name, MediaPipeline pipeline)
	{
		this.name = name;
		this.pipeline = pipeline;

	}

	public String getName()
	{
		return name;
	}

	public UserSession join(String userName, WebSocketSession session)
	{
		return null;
		//final 

	}

}
