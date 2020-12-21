package com.milkit.app.service.pay.distribute;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.common.AppCommon;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.domain.distribute.token.service.TokenGenerateHandlerServiceImpl;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DistributeHandlerServiceImpl {

	@Autowired
	private DistributeServiceImpl distributeService;
    
    @Autowired
    private TokenGenerateHandlerServiceImpl tokenGenerateHandlerService;
    

    public String distribute(DistributeRequest distributeRequest) throws Exception {
        Distribute distribute = generateDistribute(distributeRequest);
        List<DistributeDetail> distributeDetail = getDistributeDetail(distribute);

        distributeService.insert(distribute, distributeDetail);

        return distribute.getToken();
	}

    
    private Distribute generateDistribute(DistributeRequest distributeRequest) throws Exception {
        Date currDate = new Date();
		Date receiveLimitTime = DateUtil.plusMin(currDate, AppCommon.RECEIVE_LIMIT_TIME_MIN);
        Date queryLimitTime = DateUtil.plusDay(currDate, AppCommon.DIST_QUERY_LIMIT_TIME_DAY);
        String token = tokenGenerateHandlerService.generateToken(distributeRequest);

		Distribute distribute = Distribute
			.builder()
			.userID(distributeRequest.getUserID())
			.roomID(distributeRequest.getRoomID())
            .amount(distributeRequest.getAmount())
			.distCount(distributeRequest.getDistCount())
            .token(token)
            .distTime(currDate)
            .receiveLimitTime(receiveLimitTime)
			.queryLimitTime(queryLimitTime)
            .build();
        
        return distribute;
    }

    public List<DistributeDetail> getDistributeDetail(Distribute distribute) {
        List<DistributeDetail> distributeDetailList = new ArrayList<DistributeDetail>();

        long totalAmount = distribute.getAmount();
        int distCount = distribute.getDistCount();

        long remainderAmount = totalAmount%distCount;

        for(int i=0; i<distCount; i++) {
            int denominator = (distCount-i);
            long detailAmount = totalAmount/denominator;

            if(remainderAmount > 0) {
                detailAmount++;
                remainderAmount--;
            }
            totalAmount = totalAmount - detailAmount;

            DistributeDetail distributeDetail = DistributeDetail
                .builder()
                .distID(distribute.getId())
                .amount(detailAmount)
                .receiveYN("N")
                .build();
            
            distributeDetailList.add(distributeDetail);
        }

        return distributeDetailList;
    }



}
