package com.example.demo.webRtc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TemplatesController {
	
	@RequestMapping("/hello") 
	public String index() {
        return "chat.html";
    } 
	
	@RequestMapping("/roomChat") 
	public String chat() {
        return "roomChat.html";
    } 
   

}
