package com.milkit.app.domain.distribute.service;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.common.DistributeSizeCommon;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.token.service.TokenGenerateHandlerServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.service.pay.token.TokenGenerateDelegateService;
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
class DistributeServiceTests {

	@Autowired
	private DistributeServiceImpl distributeService;

	@Autowired
	private DistributeDetailServiceImpl distributeDetailService;

	@Autowired
    private TokenGenerateHandlerServiceImpl tokenGenerateHandlerService;


	@Test
	@DisplayName("1. 뿌리기정보를 등록한다.")
	public void insert_test() throws Exception {
		Date currDate = new Date();
		Date receiveLimitTime = DateUtil.plusMin(currDate, 10);
		Date queryLimitTime = DateUtil.plusDay(currDate, 7);

		Distribute distribute = Distribute
			.builder()
			.userID("032345678")
			.roomID("testroom")
			.amount(10000l)
			.distCount(3)
			.token(tokenGenerateHandlerService.generateToken(new DistributeRequest()))
			.distTime(currDate)
			.receiveLimitTime(receiveLimitTime)
			.queryLimitTime(queryLimitTime)
			.build();

		Distribute result = distributeService.insert(distribute);

log.debug("result:"+result);		
		assertTrue(result.getId() > 0l);
	}

	@Test
	@DisplayName("2. 대화방정보와Token으로 뿌리기 정보를 조회한다.")
	public void getDistributeRoomIDAndToken_test() throws Exception {
		Date currDate = new Date();
		Date receiveLimitTime = DateUtil.plusMin(currDate, 10);
		Date queryLimitTime = DateUtil.plusDay(currDate, 7);

		Distribute distribute = Distribute
			.builder()
			.userID("032345678")
			.roomID("testroom2")
			.amount(10000l)
			.distCount(3)
			.token(tokenGenerateHandlerService.generateToken(new DistributeRequest()))
			.distTime(currDate)
			.receiveLimitTime(receiveLimitTime)
			.queryLimitTime(queryLimitTime)
			.build();

		Distribute tmpResult = distributeService.insert(distribute);

		Distribute result = distributeService.getDistribute(tmpResult.getToken());
log.debug("result:"+result);

		assertTrue(result.getId() == tmpResult.getId());
	}


}
