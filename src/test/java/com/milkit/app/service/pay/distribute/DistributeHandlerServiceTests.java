package com.milkit.app.service.pay.distribute;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.common.AppCommon;
import com.milkit.app.common.DistributeSizeCommon;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distribute.token.service.TokenGenerateHandlerServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.service.pay.distribute.DistributeHandlerServiceImpl;
import com.milkit.app.service.pay.distribute.validate.DistributeRequestValidateDelegateServiceImpl;
import com.milkit.app.service.pay.token.TokenGenerateDelegateService;
import com.milkit.app.util.DateUtil;
import com.milkit.app.util.StringUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class DistributeHandlerServiceTests {

	@Autowired
	private DistributeRequestValidateDelegateServiceImpl distributeRequestValidateDelegateService;

	@Autowired
	private DistributeHandlerServiceImpl distributeHandlerService;

	@Autowired
	private DistributeServiceImpl distributeService;


	@Test
	@DisplayName("1. 뿌리기를 수행하고 토큰을 생성한다.")
	public void distribute_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		long amount = 10000l;
		int distCount = 3;

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest request = new DistributeRequest();
		request.setAmount(amount);
		request.setDistCount(distCount);

		DistributeRequest distributeRequest = distributeRequestValidateDelegateService.process(headers, request);

		String result = distributeHandlerService.distribute(distributeRequest);
log.debug("result:"+result);

		Distribute distribute = distributeService.getDistribute(result);
log.debug("distribute:"+distribute);

		assertTrue(result != null && distribute != null);
	}

	@Test
	@DisplayName("2. 뿌리기 금액이 없을 경우 예외를 테스트 한다.")
	public void distribute_amount_exception_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		long amount = 0;
		int distCount = 3;

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest request = new DistributeRequest();
		request.setAmount(amount);
		request.setDistCount(distCount);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			DistributeRequest distributeRequest = distributeRequestValidateDelegateService.process(headers, request);
			String result = distributeHandlerService.distribute(distributeRequest);
		});

log.debug(exception.getMessage());
        
        assertTrue( exception.getCode().equals("604"));
	}

	@Test
	@DisplayName("3. 뿌리기 인원이 없을 경우 예외를 테스트 한다.")
	public void distribute_distCount_exception_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		long amount = 10000;
		int distCount = 0;

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest request = new DistributeRequest();
		request.setAmount(amount);
		request.setDistCount(distCount);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			DistributeRequest distributeRequest = distributeRequestValidateDelegateService.process(headers, request);
			String result = distributeHandlerService.distribute(distributeRequest);
		});

log.debug(exception.getMessage());
        
        assertTrue( exception.getCode().equals("605"));
	}

	@Test
	@DisplayName("4. 뿌리기 금액이 인원보다 작을 경우 예외를 테스트 한다.")
	public void distribute_amount_notenough_exception_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		long amount = 3;
		int distCount = 5;

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest request = new DistributeRequest();
		request.setAmount(amount);
		request.setDistCount(distCount);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			DistributeRequest distributeRequest = distributeRequestValidateDelegateService.process(headers, request);
			String result = distributeHandlerService.distribute(distributeRequest);
		});

log.debug(exception.getMessage());
        
        assertTrue( exception.getCode().equals("606"));
	}

	@Test
	@DisplayName("5. 뿌리기 세부정보가 정상적으로 분배되어 생성되는지 확인한다.")
	public void getDistributeDetail_test() throws Exception {
		for(int i=0; i<10; i++) {
			Long amount = ThreadLocalRandom.current().nextLong(1000, 10000);
			int distCount = ThreadLocalRandom.current().nextInt(10, 100);

log.debug("amount:"+amount);
log.debug("distCount:"+distCount);

			Distribute distribute = Distribute
				.builder()
				.id(1l)
				.amount(amount)
				.distCount(distCount)
				.build();

			List<DistributeDetail> result = distributeHandlerService.getDistributeDetail(distribute);
//log.debug("result:"+result);

			Long sumAmount = getReceiveAmountSum(result);

			assertTrue(result.size() == distCount && sumAmount.equals(amount));		//랜덤으로 생성된 뿌리기 세부정보 list크기와 분배금액의 합계가 맞는지 테스트
		}
	}

	private Long getReceiveAmountSum(List<DistributeDetail> list) throws Exception {
		if(list != null) {
			return list.stream().mapToLong(x -> x.getAmount()).sum();
		} else {
			return 0l;
		}
	}
	




}
