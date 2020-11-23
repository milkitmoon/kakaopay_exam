package com.milkit.app.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppCommon {
	
    public static final String DIST_USER_HEADER_STRING = "X-USER-ID";
    public static final String DIST_ROOM_HEADER_STRING = "X-ROOM-ID";

    public static final int RECEIVE_LIMIT_TIME_MIN = 10;
    public static final int DIST_QUERY_LIMIT_TIME_DAY = 7;
	
}
