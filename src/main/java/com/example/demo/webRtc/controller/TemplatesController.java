package com.example.demo.webRtc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.webRtc.common.ApiResponse;
import com.example.demo.webRtc.common.RequestAttribute;
import com.example.demo.webRtc.mapper.MeetingMapper;

@Controller
public class TemplatesController {
	@Autowired
	private MeetingMapper meetingMapper;
	
	@ResponseBody
	@RequestMapping("/hello") 
	public ApiResponse index(@RequestAttribute WebRtctestRequest resuest) {
		
		WebRtctestResponse response = new WebRtctestResponse();
		String token = resuest.getToken();
		String ww = resuest.getWw();
		response.setToken(token);
		response.setWw(ww);
//		Meeting meeting = new Meeting();
//		meeting.setSubject("ceshi");
//		meeting.setHostName("awerty");
//		meeting.setMaxmembers(5);
//		meeting.setPreStartTime(new Date());
//		meetingMapper.insert(meeting);
        return response;
    } 
	
	@RequestMapping("/roomChat") 
	public String chat() {
        return "roomChat.html";
    } 
	
	@RequestMapping("/xhChat") 
	public String xhChat() {
        return "xhchat.html";
    } 
   

}
