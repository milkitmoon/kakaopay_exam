package com.milkit.app.service.pay.receive;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.AppCommon;
import com.milkit.app.common.DistributeSizeCommon;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.domain.distributedetail.service.DistributeDetailServiceImpl;
import com.milkit.app.service.pay.distribute.DistributeHandlerServiceImpl;
import com.milkit.app.service.pay.distribute.validate.DistributeRequestValidateDelegateServiceImpl;
import com.milkit.app.service.pay.receive.validate.ReceiveRequestValidateDelegateServiceImpl;
import com.milkit.app.service.pay.token.TokenGenerateDelegateService;
import com.milkit.app.util.DateUtil;
import com.milkit.app.util.StringUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ReceiveHandlerServiceTests {

	@Autowired
	private ReceiveRequestValidateDelegateServiceImpl receiveRequestValidateDelegateService;

	@Autowired
	private DistributeHandlerServiceImpl distributeHandlerService;

	@Autowired
	private ReceiveHandlerServiceImpl recieveHandlerService;

	@Autowired
	private DistributeServiceImpl distributeService;

	@Autowired
	private DistributeDetailServiceImpl distributeDetailService;

	@Test
	@DisplayName("1. 뿌리기 토큰으로 금액을 받는다.")
	public void receive_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		String receiveUserID = "932345678";
		long amount = 10000l;
		int distCount = 3;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest request = new ReceiveRequest();
		request.setToken(token);

		Long result = recieveHandlerService.process(headers, request);

		log.debug("result:" + result);

		Distribute distribute = distributeService.getDistribute(token);
		DistributeDetail distributeDetail = distributeDetailService.getDistributeDetail(distribute.getId(),
				receiveUserID);

		assertTrue(result.equals(distributeDetail.getAmount()));
	}

	@Test
	@DisplayName("2. 동일 사용자가 뿌리기를 다시 받을 경우 예외를 테스트한다.")
	public void receive_manytime_exception_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		String receiveUserID = "932345678";
		long amount = 10000l;
		int distCount = 3;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest request = new ReceiveRequest();
		request.setToken(token);

		Long firstResult = recieveHandlerService.process(headers, request);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			Long result = recieveHandlerService.process(headers, request);
		});

		log.debug(exception.getMessage());

		assertTrue(exception.getCode().equals("610"));
	}

	@Test
	@DisplayName("3. 뿌리기한 사용자가 받을 경우 예외를 테스트한다.")
	public void receive_own_distribute_exception_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		long amount = 10000l;
		int distCount = 3;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest request = new ReceiveRequest();
		request.setToken(token);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			Long result = recieveHandlerService.process(headers, request);
		});

		log.debug(exception.getMessage());

		assertTrue(exception.getCode().equals("608"));
	}

	@Test
	@DisplayName("4. 뿌리기와 다른방의 사용자가 받을 경우 예외를 테스트한다.")
	public void receive_differentroom_exception_test() throws Exception {
		String roomID = "testroom2";
		String differentRoomID = "testroom3";
		String userID = "032345678";
		String receiveUserID = "932345678";
		long amount = 10000l;
		int distCount = 3;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, differentRoomID);
		ReceiveRequest request = new ReceiveRequest();
		request.setToken(token);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			Long result = recieveHandlerService.process(headers, request);
		});

		log.debug(exception.getMessage());

		assertTrue(exception.getCode().equals("609"));
	}

	@Test
	@DisplayName("5. 뿌리기 받기의 시간이 초과한 경우 예외를 테스트한다.")
	public void receive_exfiredtime_exception_test() throws Exception {
		String roomID = "testroom2";
		String userID = "032345678";
		String receiveUserID = "932345678";
		long amount = 10000l;
		int distCount = 3;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		Distribute distribute = distributeService.getDistribute(token);
		distribute.setReceiveLimitTime(new Date()); // 받기제한 시간을 현시간으로 임의로 수정
		distributeService.insert(distribute);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest request = new ReceiveRequest();
		request.setToken(token);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			Long result = recieveHandlerService.process(headers, request);
		});

		log.debug(exception.getMessage());

		assertTrue(exception.getCode().equals("611"));
	}

	@Test
	@DisplayName("6. 이미 모든사용자가 받기가 완료된 후 사용자가 받기를 시도할 경우 예외를 테스트한다.")
	public void receive_alreadyreicevedall_exception_test() throws Exception {
		String roomID = "rtestroom7";
		String userID = "0832345678";
		String receiveUserID1 = "832345678";
		String receiveUserID2 = "842345678";
		String receiveUserID3 = "842345672";
		Long amount = 10000l;
		int distCount = 2;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		HttpHeaders receiveHeaders1 = new HttpHeaders();
		receiveHeaders1.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID1);
		receiveHeaders1.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest receiveRequest1 = new ReceiveRequest();
		receiveRequest1.setToken(token);

		Long receive1 = recieveHandlerService.process(receiveHeaders1, receiveRequest1);

		HttpHeaders receiveHeaders2 = new HttpHeaders();
		receiveHeaders2.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID2);
		receiveHeaders2.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest receiveRequest2 = new ReceiveRequest();
		receiveRequest2.setToken(token);

		Long receive2 = recieveHandlerService.process(receiveHeaders2, receiveRequest2);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID3);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		ReceiveRequest request = new ReceiveRequest();
		request.setToken(token);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			Long result = recieveHandlerService.process(headers, request);
		});

		log.debug(exception.getMessage());

		assertTrue(exception.getCode().equals("612"));
	}

	@Test
	@DisplayName("99. 동시에 받기 테스트한다.")
	public void receive_concurrently_test() throws Exception {
		String roomID = "rtestroom77";
		String userID = "08323456784";

		Long amount = 1000l;
		int distCount = 5;

		HttpHeaders distHeaders = new HttpHeaders();
		distHeaders.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		distHeaders.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		DistributeRequest distRequest = new DistributeRequest();
		distRequest.setAmount(amount);
		distRequest.setDistCount(distCount);

		String token = distributeHandlerService.process(distHeaders, distRequest);

		int loopCount = 100;
		ExecutorService es = Executors.newFixedThreadPool(10);
		CountDownLatch countDownLatch = new CountDownLatch(loopCount);
		for (int i = 0; i < loopCount; i++) {
			es.execute(() -> {
				String receiveUserID = RandomStringUtils.random(10, "0123456789");

				HttpHeaders receiveHeaders1 = new HttpHeaders();
				receiveHeaders1.set(AppCommon.DIST_USER_HEADER_STRING, receiveUserID);
				receiveHeaders1.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
				ReceiveRequest receiveRequest1 = new ReceiveRequest();
				receiveRequest1.setToken(token);

				try {
//					TimeUnit.MILLISECONDS.sleep(100);
					Long result = recieveHandlerService.process(receiveHeaders1, receiveRequest1);
log.debug("result:"+result);
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
				countDownLatch.countDown();
			});
		}

		countDownLatch.await(30, TimeUnit.SECONDS); 
		es.shutdown();
	}
}
