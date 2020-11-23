package com.milkit.app.domain.distribute.token.service;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.common.DistributeSizeCommon;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.util.DateUtil;
import com.milkit.app.util.StringUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class TokenGenerateHandlerServiceTests {

	@Autowired
	private DistributeServiceImpl distributeService;


	@Autowired
	private TokenGenerateHandlerServiceImpl tokenGenerateHandlerService;


	@Test
	@DisplayName("1. 토큰생성 Handler에서 토큰을 생성한다.")
	public void generateToken_test() throws Exception {
		Date currDate = new Date();
		Date receiveLimitTime = DateUtil.plusMin(currDate, 10);
		Date queryLimitTime = DateUtil.plusDay(currDate, 7);
		String roomID = "testroom2";
		DistributeRequest distributeRequest = new DistributeRequest();
		distributeRequest.setRoomID(roomID);
		String token = tokenGenerateHandlerService.generateToken(distributeRequest);

		Distribute distribute = Distribute
			.builder()
			.userID("032345678")
			.roomID(roomID)
			.amount(10000l)
			.distCount(3)
			.token(token)
			.distTime(currDate)
			.receiveLimitTime(receiveLimitTime)
			.queryLimitTime(queryLimitTime)
			.build();

		Distribute tmpResult = distributeService.insert(distribute);

		String result = tokenGenerateHandlerService.generateToken(distributeRequest);

		assertTrue(result != tmpResult.getToken());
	}


}
