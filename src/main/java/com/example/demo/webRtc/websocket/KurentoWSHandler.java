package com.example.demo.webRtc.websocket;

import java.io.IOException;

import org.kurento.client.IceCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.webRtc.entity.Room;
import com.example.demo.webRtc.entity.RoomManager;
import com.example.demo.webRtc.entity.UserRegistry;
import com.example.demo.webRtc.entity.UserSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class KurentoWSHandler extends TextWebSocketHandler
{

	private final static Gson gson = new GsonBuilder().create();
	@Autowired
	private RoomManager roomManager;

	@Autowired
	private UserRegistry registry;

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
			throws Exception
	{
		UserSession user = registry.getBySession(session);
		Room room = roomManager.getRoom(user.getRoomName());
		room.leave(user);
		registry.removeBySession(session);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception
	{
		// TODO Auto-generated method stub
		super.afterConnectionEstablished(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
			throws Exception
	{
		final JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
		UserSession user = registry.getBySession(session);
		switch (jsonMessage.get("action").getAsString())
		{
		case "joinRoom":
			joinRoom(jsonMessage, session);
			break;
		case "receiveVideoFrom":
			final String senderName = jsonMessage.get("sender").getAsString();
			final UserSession sender = registry.getByName(senderName);
			final String sdpOffer = jsonMessage.get("sdpOffer").getAsString();
			user.receiveVideoFrom(sender, sdpOffer);
			break;
		case "leaveRoom":
			leaveRoom(user);
			break;
		case "onIceCandidate":
			JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();
			if (user != null)
			{
				IceCandidate can = new IceCandidate(candidate.get("candidate").getAsString(), candidate.get("sdpMid").getAsString(),
						candidate.get("sdpMLineIndex").getAsInt());
				user.addCandidate(can, jsonMessage.get("name").getAsString());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception)
			throws Exception
	{
		UserSession user = registry.getBySession(session);
		Room room = roomManager.getRoom(user.getRoomName());
		room.leave(user);
		registry.removeBySession(session);
		exception.printStackTrace();
	}

	private void leaveRoom(UserSession user)
			throws IOException
	{
		Room room = roomManager.getRoom(user.getRoomName());
		room.leave(user);
		if (room.getParticipants().isEmpty())
			roomManager.removeRoom(room);

	}

	private void joinRoom(JsonObject jsonMessage, WebSocketSession session)
			throws IOException
	{
		String roomNmae = jsonMessage.get("room").getAsString();
		String name = jsonMessage.get("name").getAsString();
		Room room = roomManager.getRoom(roomNmae);
		UserSession user = room.join(name, session);
		registry.register(user);

	}

}