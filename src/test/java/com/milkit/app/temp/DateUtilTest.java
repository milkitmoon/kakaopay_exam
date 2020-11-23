package com.milkit.app.temp;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertTrue;


import java.util.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.milkit.app.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtilTest {

	@Test
	@DisplayName("1. 비교대상 시간의 차이를 비교한다.")
	public void compareDate_TEST() throws Exception {

		Date currDate = new Date();
		Date compareDate = DateUtil.plusMin(currDate, 1);
log.debug("currDate	:"+DateUtil.getFormatedTimeString(currDate, "yyyy-MM-dd HH:mm:ss SSS"));
log.debug("compareDate:"+DateUtil.getFormatedTimeString(compareDate, "yyyy-MM-dd HH:mm:ss SSS"));
		
		int result = DateUtil.compareDate(currDate, compareDate);
    	
log.debug("result:"+result);
		
		assertTrue(result > 0);
    }
    
}
