package com.milkit.app.service.pay.query;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.api.pay.request.QueryRequest;
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
import com.milkit.app.service.pay.query.validate.QueryRequestValidateDelegateServiceImpl;
import com.milkit.app.service.pay.receive.ReceiveHandlerServiceImpl;
import com.milkit.app.service.pay.receive.validate.ReceiveRequestValidateDelegateServiceImpl;
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
class QueryHandlerServiceTests {

	@Autowired
	private QueryRequestValidateDelegateServiceImpl queryRequestValidateDelegateService;

	@Autowired
	private DistributeHandlerServiceImpl distributeHandlerService;

	@Autowired
	private ReceiveHandlerServiceImpl recieveHandlerService;

	@Autowired
	private QueryHandlerServiceImpl queryHandlerService;

	@Autowired
	private DistributeServiceImpl distributeService;

	@Autowired
	private DistributeDetailServiceImpl distributeDetailService;


	@Test
	@DisplayName("1. 자신의 뿌리기 정보를 조회한다.")
	public void query_test() throws Exception {
		String roomID = "qtestroom7";
		String userID = "0832345678";
		String receiveUserID1 = "832345678";
		String receiveUserID2 = "842345678";
		Long amount = 10000l;
		int distCount = 3;

		DistributeRequest distributeRequest = new DistributeRequest();
		distributeRequest.setUserID(userID);
		distributeRequest.setRoomID(roomID);
		distributeRequest.setAmount(amount);
		distributeRequest.setDistCount(distCount);

		String token = distributeHandlerService.distribute(distributeRequest);

		ReceiveRequest receiveRequest1 = new ReceiveRequest();
		receiveRequest1.setUserID(receiveUserID1);
		receiveRequest1.setRoomID(roomID);
		receiveRequest1.setToken(token);

		Long receive1 = recieveHandlerService.receive(receiveRequest1);

		ReceiveRequest receiveRequest2 = new ReceiveRequest();
		receiveRequest2.setUserID(receiveUserID2);
		receiveRequest2.setRoomID(roomID);
		receiveRequest2.setToken(token);

		Long receive2 = recieveHandlerService.receive(receiveRequest2);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		QueryRequest request = new QueryRequest();
		request.setToken(token);

		QueryRequest queryRequest = queryRequestValidateDelegateService.process(headers, request);

		Distribute result = queryHandlerService.query(queryRequest);
		List<DistributeDetail> distributeDetail = result.getDetail();

log.debug("result:"+result);	

		assertTrue(	
			result.getAmount().equals(amount) &&
			distributeDetail.size() == 2 && 
			result.getReceiveAmount().equals(receive1+receive2)
		);
	}

	@Test
	@DisplayName("2. 자신의 뿌리기가 아닌 정보를 조회할 경우 예외를 테스트한다.")
	public void query_not_own_distribute_exception_test() throws Exception {
		String roomID = "qtestroom7";
		String userID = "0832345678";
		String receiveUserID1 = "832345678";
		String receiveUserID2 = "842345678";
		String queryUserID = "7832345678";
		Long amount = 10000l;
		int distCount = 3;

		DistributeRequest distributeRequest = new DistributeRequest();
		distributeRequest.setUserID(userID);
		distributeRequest.setRoomID(roomID);
		distributeRequest.setAmount(amount);
		distributeRequest.setDistCount(distCount);

		String token = distributeHandlerService.distribute(distributeRequest);

		ReceiveRequest receiveRequest1 = new ReceiveRequest();
		receiveRequest1.setUserID(receiveUserID1);
		receiveRequest1.setRoomID(roomID);
		receiveRequest1.setToken(token);

		recieveHandlerService.receive(receiveRequest1);

		ReceiveRequest receiveRequest2 = new ReceiveRequest();
		receiveRequest2.setUserID(receiveUserID2);
		receiveRequest2.setRoomID(roomID);
		receiveRequest2.setToken(token);

		recieveHandlerService.receive(receiveRequest2);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, queryUserID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		QueryRequest request = new QueryRequest();
		request.setToken(token);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			QueryRequest queryRequest = queryRequestValidateDelegateService.process(headers, request);
			Distribute result = queryHandlerService.query(queryRequest);
		});

log.debug(exception.getMessage());
	
		assertTrue( exception.getCode().equals("613"));
	}

	@Test
	@DisplayName("3. 뿌리기 조회시간을 초과했을 경우 예외를 테스트한다.")
	public void query_exfiredtime_exception_test() throws Exception {
		String roomID = "qtestroom7";
		String userID = "0832345678";
		String receiveUserID1 = "832345678";
		String receiveUserID2 = "842345678";
		Long amount = 10000l;
		int distCount = 3;

		DistributeRequest distributeRequest = new DistributeRequest();
		distributeRequest.setUserID(userID);
		distributeRequest.setRoomID(roomID);
		distributeRequest.setAmount(amount);
		distributeRequest.setDistCount(distCount);

		String token = distributeHandlerService.distribute(distributeRequest);

		ReceiveRequest receiveRequest1 = new ReceiveRequest();
		receiveRequest1.setUserID(receiveUserID1);
		receiveRequest1.setRoomID(roomID);
		receiveRequest1.setToken(token);

		recieveHandlerService.receive(receiveRequest1);

		ReceiveRequest receiveRequest2 = new ReceiveRequest();
		receiveRequest2.setUserID(receiveUserID2);
		receiveRequest2.setRoomID(roomID);
		receiveRequest2.setToken(token);

		recieveHandlerService.receive(receiveRequest2);

		Distribute distribute = distributeService.getDistribute(token);
		distribute.setQueryLimitTime(new Date());				//조회 제한시간을 현시간으로 임의로 수정
		distributeService.insert(distribute);

		HttpHeaders headers = new HttpHeaders();
		headers.set(AppCommon.DIST_USER_HEADER_STRING, userID);
		headers.set(AppCommon.DIST_ROOM_HEADER_STRING, roomID);
		QueryRequest request = new QueryRequest();
		request.setToken(token);

		ServiceException exception = assertThrows(ServiceException.class, () -> {
			QueryRequest queryRequest = queryRequestValidateDelegateService.process(headers, request);
			Distribute result = queryHandlerService.query(queryRequest);
		});

log.debug(exception.getMessage());
	
		assertTrue( exception.getCode().equals("614"));
	}

}
