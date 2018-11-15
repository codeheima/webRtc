package com.example.demo.webRtc.entity;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kurento.client.Composite;
import org.kurento.client.Continuation;
import org.kurento.client.HubPort;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CompositeRoom implements Closeable
{
	private final ConcurrentMap<String, CompositeUserSession> participants = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;
	private final String name;

	private Composite composite = null;

	private RecorderEndpoint recorderEndpoint;

	private HubPort compoisteOutputHubport;

	public HubPort getCompositeOutputHubport()
	{
		return compoisteOutputHubport;
	}

	@Override
	public void close()
	{
		for (final CompositeUserSession user : participants.values())
		{
			try
			{
				user.close();
			} catch (IOException e)
			{
			}
		}

		participants.clear();

		pipeline.release(new Continuation<Void>()
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

	public CompositeRoom(String name, MediaPipeline pipeline)
	{
		this.name = name;
		this.pipeline = pipeline;
		this.composite = new Composite.Builder(pipeline).build();
		this.compoisteOutputHubport = new HubPort.Builder(composite).build();

	}

	public String getName()
	{
		return name;
	}

	public CompositeUserSession join(String userName, WebSocketSession session)
			throws IOException
	{
		// 创建集线器
		final CompositeUserSession participant = new CompositeUserSession(userName, this.name, session, this.pipeline, this.composite,
				this.compoisteOutputHubport);
		if (participants.size() == 1)
		{
			this.recorderEndpoint = new RecorderEndpoint.Builder(pipeline,
					"file:///home/clouder/Desktop" + getName() + ".webm").withMediaProfile(MediaProfileSpecType.MP4)
							.build();
			compoisteOutputHubport.connect(recorderEndpoint);
			recorderEndpoint.connect(compoisteOutputHubport);
			recorderEndpoint.record();
		}
		joinRoom(participant);
		participants.put(participant.getName(), participant);
		// 提醒其他成員
		sendParticipantNames(participant);
		return participant;

	}

	public void sendParticipantNames(CompositeUserSession user)
			throws IOException
	{

		final JsonArray participantsArray = new JsonArray();
		for (final CompositeUserSession participant : this.getParticipants())
		{
			if (!participant.equals(user))
			{
				final JsonElement participantName = new JsonPrimitive(participant.getName());
				participantsArray.add(participantName);
			}
		}
		final JsonObject existingParticipantsMsg = new JsonObject();
		existingParticipantsMsg.addProperty("action", "existingParticipants");
		existingParticipantsMsg.add("data", participantsArray);
		user.sendMessage(existingParticipantsMsg);
	}

	private Collection<String> joinRoom(CompositeUserSession participant2)
	{
		final JsonObject newParticipantMsg = new JsonObject();
		newParticipantMsg.addProperty("action", "newParticipantArrived");
		newParticipantMsg.addProperty("name", participant2.getName());

		final List<String> participantsList = new ArrayList<>(participants.values().size());

		for (final CompositeUserSession participant : participants.values())
		{
			try
			{
				participant.sendMessage(newParticipantMsg);
			} catch (final IOException e)
			{
				e.printStackTrace();
			}
			participantsList.add(participant.getName());
		}

		return participantsList;

	}

	public Collection<CompositeUserSession> getParticipants()
	{
		return participants.values();
	}

	public void leave(UserSession user)
			throws IOException
	{
		this.removeParticipant(user.getName());
		user.close();
	}

	private void removeParticipant(String name)
			throws IOException
	{
		participants.remove(name);
		final List<String> unnotifiedParticipants = new ArrayList<>();
		final JsonObject participantLeftJson = new JsonObject();
		participantLeftJson.addProperty("action", "participantLeft");
		participantLeftJson.addProperty("name", name);
		for (final CompositeUserSession participant : participants.values())
		{
			try
			{
				participant.cancelVideoFrom(name);
				participant.sendMessage(participantLeftJson);
			} catch (final IOException e)
			{
				unnotifiedParticipants.add(participant.getName());
			}
		}
	}

}
