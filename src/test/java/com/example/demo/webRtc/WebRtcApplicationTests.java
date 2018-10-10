package com.example.demo.webRtc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.webRtc.entity.Meeting;
import com.example.demo.webRtc.mapper.MeetingMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebRtcApplicationTests {
	@Autowired
	private MeetingMapper meetingMapper;

	@Test
	public void testInsert() throws Exception {
		Meeting meeting = new Meeting();
		meeting.setSubject("ceshi");
		meeting.setHostName("awerty");
		meeting.setMaxmembers(5);
		meetingMapper.insert(meeting);
		

		//Assert.assertEquals(3, UserMapper.getAll().size());
	}
	

}
