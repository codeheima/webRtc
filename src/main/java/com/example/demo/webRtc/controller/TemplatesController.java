package com.example.demo.webRtc.controller;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.webRtc.entity.Meeting;
import com.example.demo.webRtc.mapper.MeetingMapper;

@Controller
public class TemplatesController {
	@Autowired
	private MeetingMapper meetingMapper;
	@RequestMapping("/hello") 
	public String index() {
		Meeting meeting = new Meeting();
		meeting.setSubject("ceshi");
		meeting.setHostName("awerty");
		meeting.setMaxmembers(5);
		meeting.setPreStartTime(new Date());
		meetingMapper.insert(meeting);
        return "chat.html";
    } 
	
	@RequestMapping("/roomChat") 
	public String chat() {
        return "roomChat.html";
    } 
   

}
