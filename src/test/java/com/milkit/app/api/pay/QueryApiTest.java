package com.milkit.app.api.pay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milkit.app.common.AppCommon;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.config.WebSecurityConfigure;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import lombok.extern.slf4j.Slf4j;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class QueryApiTest {

    @Autowired
    private MockMvc mvc;
	
	@Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DistributeServiceImpl distributeService;
    
    @Test
	@DisplayName("1. 자신의 뿌리기 정보를 조회한다.")
	public void query_test() throws Exception {
        String roomID = "dweRe4F432td2";
		String userID = "01049434678";
		String receiveUserID1 = "010562345678";
		String receiveUserID2 = "010782345678";
		long amount = 8000l;
        int distCount = 5;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
        String token = tmpResult.getValue();
        
        String content = "{\"token\":\""+token+"\"}";
        
        //첫번째 사용자 받기
        mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID1)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"));

        //두번째 사용자 받기
        mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID2)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/api/pay/query/"+token)
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value.amount").value(amount))
                .andExpect(jsonPath("value.detail.length()").value(2))
    			;

    }

    @Test
	@DisplayName("2. 자신의 뿌리기가 아닌 정보를 조회할 경우 예외를 테스트한다.")
	public void query_not_own_distribute_exception_test() throws Exception {
        String roomID = "e3weRe4F432td2";
		String userID = "01019434678";
		String receiveUserID1 = "010662345678";
        String queryUserID = "09832345678";
		long amount = 8000l;
        int distCount = 5;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
        String token = tmpResult.getValue();
        
        String content = "{\"token\":\""+token+"\"}";
        
        //첫번째 사용자 받기
        mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID1)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/api/pay/query/"+token)
            .header(AppCommon.DIST_USER_HEADER_STRING, queryUserID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("613"))
                .andExpect(jsonPath("value").isEmpty())
    			;
    }

    @Test
	@DisplayName("3. 뿌리기 조회시간을 초과했을 경우 예외를 테스트한다.")
	public void query_exfiredtime_exception_test() throws Exception {
        String roomID = "dweRe4F432td2";
		String userID = "01049434678";
		String receiveUserID1 = "010462345678";
		long amount = 8000l;
        int distCount = 5;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
        String token = tmpResult.getValue();
        
        String content = "{\"token\":\""+token+"\"}";
        
        //첫번째 사용자 받기
        mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID1)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"));

        Distribute distribute = distributeService.getDistribute(token);
		distribute.setQueryLimitTime(new Date());				//조회 제한시간을 현시간으로 임의로 수정
		distributeService.insert(distribute);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.get("/api/pay/query/"+token)
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("614"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }
    
}
