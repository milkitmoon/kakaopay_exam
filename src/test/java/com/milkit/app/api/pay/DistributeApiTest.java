package com.milkit.app.api.pay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milkit.app.common.AppCommon;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.config.WebSecurityConfigure;

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


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class DistributeApiTest {

    @Autowired
    private MockMvc mvc;
	
	@Autowired
    private ObjectMapper objectMapper;
    

    @Test
    @DisplayName("1. 뿌리기를 수행하고 토큰을 생성한다.")
    public void distribute_test() throws Exception {
        String roomID = "r3ew32td2";
		String userID = "0109684318";
		long amount = 10000l;
        int distCount = 3;
        
    	String content = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute")
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").isNotEmpty())
    			;

    }

    @Test
    @DisplayName("2. 뿌리기 금액이 없을 경우 예외를 테스트 한다.")
    public void distribute_amount_exception_test() throws Exception {
        String roomID = "r3ew32td2";
		String userID = "0109684318";
		long amount = 10000l;
        int distCount = 3;
        
    	String content = "{\"distCount\":"+distCount+"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute")
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("604"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }

    @Test
    @DisplayName("3. 뿌리기 인원이 없을 경우 예외를 테스트 한다.")
    public void distribute_distCount_exception_test() throws Exception {
        String roomID = "r3ew32td2";
		String userID = "0109684318";
		long amount = 10000l;
        int distCount = 0;
        
    	String content = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute")
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("605"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }

    @Test
    @DisplayName("4. 뿌리기 금액이 인원보다 작을 경우 예외를 테스트 한다.")
    public void distribute_amount_notenough_exception_test() throws Exception {
        String roomID = "r3ew32td2";
		String userID = "0109684318";
		long amount = 3;
        int distCount = 5;
        
    	String content = "{\"amount\":"+amount+", \"distCount\":"+distCount+"}";
    	
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/api/pay/distribute")
            .header(AppCommon.DIST_USER_HEADER_STRING, userID)
            .header(AppCommon.DIST_ROOM_HEADER_STRING, roomID)
            .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("606"))
                .andExpect(jsonPath("value").isEmpty())
    			;

    }
    
}
