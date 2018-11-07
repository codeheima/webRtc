package com.example.demo.webRtc.entity;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kurento.client.EventListener;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;

public class UserSession
{
	private final String name;
	private final WebSocketSession session;

	private final MediaPipeline pipeline;

	private final String roomName;
	private final WebRtcEndpoint outgoingMedia;
	private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

	public UserSession(final String name, String roomName, final WebSocketSession session, MediaPipeline pipeline)
	{

		this.pipeline = pipeline;
		this.name = name;
		this.session = session;
		this.roomName = roomName;
		this.outgoingMedia = new WebRtcEndpoint.Builder(pipeline).build();

		this.outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>()
		{

			@Override
			public void onEvent(IceCandidateFoundEvent event)
			{
				JsonObject response = new JsonObject();
				response.addProperty("id", "iceCandidate");
				response.addProperty("name", name);
				response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
				try
				{
					synchronized (session)
					{
						session.sendMessage(new TextMessage(response.toString()));
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public WebRtcEndpoint getOutgoingWebRtcPeer()
	{
		return outgoingMedia;
	}

	public String getName()
	{
		return name;
	}

	public WebSocketSession getSession()
	{
		return session;
	}

	public String getRoomName()
	{
		return this.roomName;
	}

}
