package com.example.demo.webRtc.entity;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Room implements Closeable
{
	private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;
	private final String name;

	@Override
	public void close()
	{
		for (final UserSession user : participants.values())
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
			throws IOException
	{
		final UserSession participant = new UserSession(userName, this.name, session, pipeline);
		joinRoom(participant);
		participants.put(participant.getName(), participant);
		// 提醒其他成員
		sendParticipantNames(participant);
		return participant;

	}

	public void sendParticipantNames(UserSession user)
			throws IOException
	{

		final JsonArray participantsArray = new JsonArray();
		for (final UserSession participant : this.getParticipants())
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

	private Collection<String> joinRoom(UserSession newParticipant)
	{
		final JsonObject newParticipantMsg = new JsonObject();
		newParticipantMsg.addProperty("action", "newParticipantArrived");
		newParticipantMsg.addProperty("name", newParticipant.getName());

		final List<String> participantsList = new ArrayList<>(participants.values().size());

		for (final UserSession participant : participants.values())
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

	public Collection<UserSession> getParticipants()
	{
		return participants.values();
	}

	public void leave(UserSession user)
			throws IOException
	{
		this.removeParticipant(name);
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
		for (final UserSession participant : participants.values())
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
