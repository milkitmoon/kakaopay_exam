package com.milkit.app.domain.distributedetail.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.milkit.app.common.DistributeSizeCommon;
import com.milkit.app.domain.distribute.Distribute;
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
class DistributeDetailServiceTests {

	@Autowired
	private DistributeDetailServiceImpl distributeDetailService;



	@Test
	@DisplayName("1. 뿌리기 세부정보를 등록한다.")
	public void insert_test() throws Exception {
		Date currDate = new Date();

		DistributeDetail distributeDetail1 = DistributeDetail
		.builder()
		.distID(1l)
		.userID("022345678")
		.amount(3300l)
		.receiveYN("N")
		.build();

		DistributeDetail result = distributeDetailService.insert(distributeDetail1);

log.debug("result:"+result);		
		assertTrue(result.getId() > 0l);
	}


	@Test
	@DisplayName("2. 특정 뿌리기 세부정보를 조회한다.")
	public void getDistributeDetail_test() throws Exception {
		Date currDate = new Date();
		long distID = 1l;
		String userID = "02234567813";

		DistributeDetail distributeDetail1 = DistributeDetail
		.builder()
		.distID(distID)
		.userID(userID)
		.amount(3300l)
		.receiveYN("Y")
		.receiveTime(currDate)
		.build();

		DistributeDetail distributeDetail = distributeDetailService.insert(distributeDetail1);

		DistributeDetail result = distributeDetailService.getDistributeDetail(distID, userID);
log.debug("result:"+result);

		assertTrue(result.getId() == distributeDetail.getId());
	}


	@Test
	@DisplayName("3. 뿌리기ID로 해당되는 세부정보를 전부 조회한다.")
	public void getDistributeDetail2_test() throws Exception {
		Date currDate = new Date();
		long distID = 1l;
		String userID = "0223456782";

		List<DistributeDetail> data = new ArrayList<DistributeDetail>();

		DistributeDetail distributeDetail1 = DistributeDetail
		.builder()
		.distID(distID)
		.userID(userID)
		.amount(3334l)
		.receiveYN("Y")
		.receiveTime(currDate)
		.build();

		DistributeDetail distributeDetail2 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		DistributeDetail distributeDetail3 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		data.add(distributeDetail1);
		data.add(distributeDetail2);
		data.add(distributeDetail3);

		distributeDetailService.insert(data);

		List<DistributeDetail> result = distributeDetailService.getDistributeDetail(distID);
log.debug("result:"+result);

		assertTrue(result.size() > 0);
	}


	@Test
	@DisplayName("4. 뿌리기세부 정보를 업데이트 한후 업데이트 한 정보를 가져온다.")
	public void updateReceive_test() throws Exception {
		Date currDate = new Date();
		long distID = 1l;
		String userID = "02234567821";

		List<DistributeDetail> data = new ArrayList<DistributeDetail>();

		DistributeDetail distributeDetail1 = DistributeDetail
		.builder()
		.distID(distID)
		.userID(userID)
		.amount(3334l)
		.receiveYN("Y")
		.receiveTime(currDate)
		.build();

		DistributeDetail distributeDetail2 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		DistributeDetail distributeDetail3 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		data.add(distributeDetail1);
		data.add(distributeDetail2);
		data.add(distributeDetail3);

		distributeDetailService.insert(data);

		String receiveUserID = "test2";
		distributeDetailService.updateReceive(distID, receiveUserID);

		DistributeDetail result = distributeDetailService.getDistributeDetail(distID, receiveUserID);
log.debug("result:"+result);

		assertTrue(result.getUserID().equals(receiveUserID));
	}

	@Test
	@DisplayName("5. 뿌리기를 받은 세부정보를 가져온다.")
	public void getDistributeDetailReceiveYN_test() throws Exception {
		Date currDate = new Date();
		long distID = 1l;
		String userID = "0323456782";

		List<DistributeDetail> data = new ArrayList<DistributeDetail>();

		DistributeDetail distributeDetail1 = DistributeDetail
		.builder()
		.distID(distID)
		.userID(userID)
		.amount(3334l)
		.receiveYN("Y")
		.receiveTime(currDate)
		.build();

		DistributeDetail distributeDetail2 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		DistributeDetail distributeDetail3 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		data.add(distributeDetail1);
		data.add(distributeDetail2);
		data.add(distributeDetail3);

		distributeDetailService.insert(data);

		List<DistributeDetail> result = distributeDetailService.getDistributeDetailReceiveYN(distID, "Y");
log.debug("result:"+result);

		assertTrue(result.get(0).getReceiveYN().equals("Y"));
	}


	@Test
	@DisplayName("6. 뿌리기를 받은 총금액을 가져온다.")
	public void getReceiveAmount_test() throws Exception {
		Date currDate = new Date();
		long distID = 1l;
		String userID = "0423456782";

		List<DistributeDetail> data = new ArrayList<DistributeDetail>();

		DistributeDetail distributeDetail1 = DistributeDetail
		.builder()
		.distID(distID)
		.userID(userID)
		.amount(3334l)
		.receiveYN("Y")
		.receiveTime(currDate)
		.build();

		DistributeDetail distributeDetail2 = DistributeDetail
		.builder()
		.distID(distID)
		.userID("6545")
		.amount(3333l)
		.receiveYN("Y")
		.receiveTime(currDate)
		.build();

		DistributeDetail distributeDetail3 = DistributeDetail
		.builder()
		.distID(distID)
		.amount(3333l)
		.receiveYN("N")
		.build();

		data.add(distributeDetail1);
		data.add(distributeDetail2);
		data.add(distributeDetail3);

		distributeDetailService.insert(data);

		Long result = distributeDetailService.getReceiveAmount(distID);
log.debug("result:"+result);

		assertTrue(result > 0l);
	}

}
