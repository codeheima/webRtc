package com.example.demo.webRtc.entity;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;

public class UserSession implements Closeable
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
				response.addProperty("action", "iceCandidate");
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

	public void sendMessage(JsonObject message)
			throws IOException
	{
		synchronized (session)
		{
			session.sendMessage(new TextMessage(message.toString()));
		}
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

	@Override
	public void close()
			throws IOException
	{
		for (final String remoteParticipantName : incomingMedia.keySet())
		{

			final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);

			ep.release(new Continuation<Void>()
			{

				@Override
				public void onSuccess(Void result)
						throws Exception
				{
				}

				@Override
				public void onError(Throwable cause)
						throws Exception
				{
				}
			});
		}

		outgoingMedia.release(new Continuation<Void>()
		{

			@Override
			public void onSuccess(Void result)
					throws Exception
			{
			}

			@Override
			public void onError(Throwable cause)
					throws Exception
			{
			}
		});

	}

	public void cancelVideoFrom(final String senderName)
	{
		final WebRtcEndpoint incoming = incomingMedia.remove(senderName);

		incoming.release(new Continuation<Void>()
		{

			@Override
			public void onSuccess(Void arg0)
					throws Exception
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0)
					throws Exception
			{
				// TODO Auto-generated method stub

			}
		});

	}

	public void receiveVideoFrom(UserSession sender, String sdpOffer)
			throws IOException
	{

		final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);
		final JsonObject scParams = new JsonObject();
		scParams.addProperty("action", "receiveVideoAnswer");
		scParams.addProperty("name", sender.getName());
		scParams.addProperty("sdpAnswer", ipSdpAnswer);

		this.sendMessage(scParams);
		this.getEndpointForUser(sender).gatherCandidates();
	}

	private WebRtcEndpoint getEndpointForUser(final UserSession sender)
	{
		if (sender.getName().equals(this.name))
			return outgoingMedia;
		WebRtcEndpoint incomming = incomingMedia.get(sender.getName());
		if (incomming == null)
		{
			incomming = new WebRtcEndpoint.Builder(pipeline).build();
			incomming.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>()
			{

				@Override
				public void onEvent(IceCandidateFoundEvent event)
				{
					JsonObject response = new JsonObject();
					response.addProperty("action", "iceCandidate");
					response.addProperty("name", sender.getName());
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
			incomingMedia.put(sender.getName(), incomming);
		}
		sender.getOutgoingWebRtcPeer().connect(incomming);

		return incomming;

	}

	public void addCandidate(IceCandidate candidate, String name)
	{
		if (this.name.compareTo(name) == 0)
		{
			outgoingMedia.addIceCandidate(candidate);
		} else
		{
			WebRtcEndpoint webRtc = incomingMedia.get(name);
			if (webRtc != null)
			{
				webRtc.addIceCandidate(candidate);
			}
		}
	}

}
