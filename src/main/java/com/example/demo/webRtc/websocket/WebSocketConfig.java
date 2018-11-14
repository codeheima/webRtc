//package com.example.demo.webRtc.websocket;
//
//import org.kurento.client.KurentoClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//
//@Configuration
//public class WebSocketConfig {
//	
//	private static final String DEFAULT_KMS="ws://192.168.1.113:8888/kurento";
//	@Bean
//	public ServerEndpointExporter serverEndpointExporter() {
//		return new ServerEndpointExporter();
//	}
//
//	@Bean
//	public KurentoClient kurentoClient() {
//		return KurentoClient.create(DEFAULT_KMS);
//	}
//
//}
