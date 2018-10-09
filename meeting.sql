-- !UP BEGIN
CREATE TABLE IF NOT EXISTS `appcc_meeting`(
	`id` BIGINT（20）NOT NULL AUTO_INCREMENT,
	`hostName` varchar(40) NOT NULL COMMENT '会议主持人'
	`subject`  varchar(40) NOT NULL COMMENT '会议主题',
	`maxmembers` INT(11) NOT NULL DEFAULT 0 COMMENT '最大与会人员'，
	`preStartTime`	timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预开始时间',
	`preEndTime`	timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预结束时间',
	`startTime`	timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会议开始时间',
	`endTime`	timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会议结束时间',
	`createAt`	timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会议创建时间',
	PRIMARY KEY (`id`)
)engine=innodb charset utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `appcc_meeting_member`(
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`meetingId` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '会议室id',
	`username` varchar(40) NOT NULL COMMENT '成员小慧号',
	`nickname` varchar(100) COMMENT '成员昵称'
	`jointime` TIMESTAMP，
	`status`  TINYINT NOT NULL DEFAULT 0 COMMENT '与会状态',
	`vediostatus` TINYINT NOT NULL DEFAULT 0 COMMENT '视频设备状态'，
	`adiostatus` TINYINT NOT NULL DEFAULT 0 COMMENT '音频设备状态'
	PRIMARY KEY (`id`),
	UNIQUE KEY IX_APPCC_MEETING_MEMBER_MEETINGID_USERNAME (`meetingId`,`username`)
)engine=innodb charset utf8mb4 COLLATE utf8mb4_general_ci;