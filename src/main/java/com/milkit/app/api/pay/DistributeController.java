package com.milkit.app.api.pay;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.milkit.app.api.AbstractApiController;
import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.api.pay.request.QueryRequest;
import com.milkit.app.api.pay.request.ReceiveRequest;
import com.milkit.app.common.exception.handler.ApiResponseEntityExceptionHandler;
import com.milkit.app.common.response.GenericResponse;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.service.pay.distribute.DistributeHandlerServiceImpl;
import com.milkit.app.service.pay.query.QueryHandlerServiceImpl;
import com.milkit.app.service.pay.receive.ReceiveHandlerServiceImpl;
import com.milkit.app.service.pay.validate.RequestValidateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@Api(tags = "1. 카카오페이 뿌리기", value = "DistributeController")
public class DistributeController extends AbstractApiController {

    
    @Autowired
    private DistributeHandlerServiceImpl distributeHandlerService;

    @Autowired
    private ReceiveHandlerServiceImpl receiveHandlerService;

    @Autowired
    private QueryHandlerServiceImpl queryHandlerService;


    @PostMapping("/api/pay/distribute")
    @ApiOperation(value = "1.1 뿌리기 API", notes = "대화방 친구들에게 금액 뿌리기를 수행한다.")
    public ResponseEntity<GenericResponse<String>> distribute(
            @ApiParam(value = "API 헤더정보", required = true) @RequestHeader HttpHeaders headers, 
            @ApiParam(value = "뿌리기 요청정보", required = true) @RequestBody final DistributeRequest request) throws Exception {

        return apiResponse(() -> distributeHandlerService.process(headers, request));
    }

    @PutMapping("/api/pay/receive")
    @ApiOperation(value = "1.2 받기 API", notes = "대화방에 뿌린 금액을 특정 사용자에게 전달한다.")
    public ResponseEntity<GenericResponse<Long>> receive(
            @ApiParam(value = "API 헤더정보", required = true) @RequestHeader HttpHeaders headers, @RequestBody 
            @ApiParam(value = "받기 요청정보", required = true) final ReceiveRequest request) throws Exception {

        return apiResponse(() -> receiveHandlerService.process(headers, request));
    }

    @GetMapping("/api/pay/query/{token}")
    @ApiOperation(value = "1.3 조회 API", notes = "뿌리기에 대한 정보를 사용자에게 전달한다.")
    public ResponseEntity<GenericResponse<Distribute>> query(
            @ApiParam(value = "API 헤더정보", required = true) @RequestHeader HttpHeaders headers, 
            @ApiParam(value = "뿌리기를 조회할 토큰정보", required = true) @PathVariable(value="token", required=true) String token) throws Exception {

        return apiResponse(() -> queryHandlerService.process(headers, new QueryRequest(token)));
    }

    
}