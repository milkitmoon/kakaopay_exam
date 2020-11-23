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
import org.mockito.internal.matchers.GreaterThan;
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
import static org.hamcrest.Matchers.*;

import java.util.Date;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ReceiveApiTest {

    @Autowired
    private MockMvc mvc;
	
	@Autowired
    private ObjectMapper objectMapper;

    @Autowired
	private DistributeServiceImpl distributeService;
    

    @Test
	@DisplayName("1. 뿌리기 토큰으로 금액을 받는다.")
	public void receive_test() throws Exception {
        String roomID = "dw3ew32td2";
        String userID = "01066849318";
        String receiveUserID = "01023684318";
		long amount = 10000l;
        int distCount = 3;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
    	String token = tmpResult.getValue();

    	String content = "{\"token\":\""+token+"\"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
            .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").value(greaterThan(0)))
    			;
    }

    @Test
	@DisplayName("2. 동일 사용자가 뿌리기를 다시 받을 경우 예외를 테스트한다.")
	public void receive_manytime_exception_test() throws Exception {
        String roomID = "dw3ew32td2";
        String userID = "01066849318";
        String receiveUserID = "01023684318";
		long amount = 10000l;
        int distCount = 3;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
    	String token = tmpResult.getValue();

    	String content = "{\"token\":\""+token+"\"}";
        
        mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
        .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
        .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"));

        mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
            .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("610"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }

    @Test
	@DisplayName("3. 뿌리기한 사용자가 받기 시도를 할 경우 예외를 테스트한다.")
	public void receive_own_distribute_exception_test() throws Exception {
        String roomID = "dw3ew32td2";
        String userID = "01066849318";
		long amount = 10000l;
        int distCount = 3;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
    	String token = tmpResult.getValue();

    	String content = "{\"token\":\""+token+"\"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("608"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }

    @Test
	@DisplayName("4. 뿌리기와 다른방의 사용자가 받을 경우 예외를 테스트한다.")
	public void receive_differentroom_exception_test() throws Exception {
        String roomID = "dw3e31w32td2";
		String differentRoomID = "tr3SruT5X8";
		String userID = "01031234678";
		String receiveUserID = "0105869393";
		long amount = 10000l;
        int distCount = 3;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
    	String token = tmpResult.getValue();

    	String content = "{\"token\":\""+token+"\"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
            .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, differentRoomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("609"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }

    @Test
	@DisplayName("5. 뿌리기 받기의 시간이 초과한 경우 예외를 테스트한다.")
	public void receive_exfiredtime_exception_test() throws Exception {
        String roomID = "dw3e31w32td2";
		String userID = "01031234678";
		String receiveUserID = "0105869393";
		long amount = 10000l;
        int distCount = 3;

        String distributeContent = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        MvcResult distributeResult = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute").header(AppCommon.DIST_USER_HEADER_STRING, userID).header(AppCommon.DIST_ROOM_HEADER_STRING, roomID).content(distributeContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andReturn();
    	String json = distributeResult.getResponse().getContentAsString();
    	GenericResponse<String> tmpResult = objectMapper.readValue(json, new TypeReference<GenericResponse<String>>() {});
        String token = tmpResult.getValue();
        
        Distribute distribute = distributeService.getDistribute(token);
		distribute.setReceiveLimitTime(new Date());				//받기제한 시간을 현시간으로 임의로 수정
		distributeService.insert(distribute);

    	String content = "{\"token\":\""+token+"\"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
            .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("611"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }

    @Test
	@DisplayName("6. 이미 모든사용자가 받기가 완료된 후 사용자가 받기를 시도할 경우 예외를 테스트한다.")
	public void receive_alreadyreicevedall_exception_test() throws Exception {
        String roomID = "dweRde31w32td2";
		String userID = "01031434678";
		String receiveUserID1 = "010832345678";
		String receiveUserID2 = "010842345678";
		String receiveUserID3 = "010842345672";
		long amount = 3000l;
        int distCount = 2;

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

    	//세번째 사용자 받기
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/pay/receive")
            .header(AppCommon.DIST_USER_HEADER_STRING, receiveUserID3)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("612"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }
    
}
