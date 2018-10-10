package com.example.demo.webRtc.mapper;

import org.apache.ibatis.annotations.Insert;

import com.example.demo.webRtc.entity.Meeting;

public interface MeetingMapper {
	
	@Insert("INSERT INTO appcc_meeting(hostName,subject,maxmembers,preStartTime) VALUES(#{hostName}, #{subject}, #{maxmembers},#{preStartTime})")
	void insert(Meeting meeting);

}
