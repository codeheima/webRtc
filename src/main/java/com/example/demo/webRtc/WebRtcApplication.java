package com.example.demo.webRtc;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.kurento.client.KurentoClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.demo.webRtc.entity.RoomManager;
import com.example.demo.webRtc.entity.UserRegistry;
import com.example.demo.webRtc.websocket.KurentoWSHandler;

@Controller
@SpringBootApplication
@MapperScan("com.example.demo.webRtc.mapper")
@EnableWebSocket
public class WebRtcApplication implements WebSocketConfigurer
{
	final static String DEFAULT_KMS_WS_URI = "ws://192.168.1.113:8888/kurento";

	public static void main(String[] args)
	{
		SpringApplication.run(WebRtcApplication.class, args);
	}

	@RequestMapping("/")
	public String index()
	{

		return "index.html";

	}

	@Bean
	public TomcatServletWebServerFactory servletContainer()
	{
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory()
		{
			@Override
			protected void postProcessContext(Context context)
			{
				SecurityConstraint constraint = new SecurityConstraint();
				constraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				constraint.addCollection(collection);
				context.addConstraint(constraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(httpConnector());
		return tomcat;
	}

	@Bean
	public Connector httpConnector()
	{
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		// Connector监听的http的端口号
		connector.setPort(8080);
		connector.setSecure(false);
		// 监听到http的端口号后转向到的https的端口号
		connector.setRedirectPort(8443);
		return connector;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
	{
		registry.addHandler(groupCallHandler(), "/groupcall");

	}

	@Bean
	public UserRegistry registry()
	{
		return new UserRegistry();
	}

	@Bean
	public RoomManager roomManager()
	{
		return new RoomManager();
	}

	@Bean
	public KurentoWSHandler groupCallHandler()
	{
		return new KurentoWSHandler();
	}

	@Bean
	public KurentoClient kurentoClient()
	{
		return KurentoClient.create(DEFAULT_KMS_WS_URI);
	}

}
