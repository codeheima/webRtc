package com.example.demo.webRtc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.example.demo.webRtc.entity.Meeting;

public interface MeetingMapper {
	
	@Insert("INSERT INTO appcc_meeting(hostName,subject,maxmembers,preStartTime) VALUES(#{hostName}, #{subject}, #{maxmembers},#{preStartTime})")
	void insert(Meeting meeting);
	
	@Select("SELECT * FROM appcc_meeting")
	@Results({
		@Result(property = "hostName",  column = "hostName"),
		@Result(property = "subject", column = "subject"),
		@Result(property = "maxmembers",column="maxmembers",javaType=Integer.class)
	})
	List<Meeting> getAll();

}
