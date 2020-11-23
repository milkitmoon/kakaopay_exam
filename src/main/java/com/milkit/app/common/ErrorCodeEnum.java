package com.milkit.app.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.milkit.app.common.pattern.PatternMacherServiceImpl;


public enum ErrorCodeEnum {
	
	OK("0", "성공했습니다."), 
	RESPONSE_OK("200", "전송이 성공하였습니다."), 
	BAD_REQUEST("201", "HTTP 전송 형식이 잘못되었습니다. HTTP 파라미터 및 BODY의 값이 정상적인지 확인해 주세요."),
	RESPONSE_FAIL("299", "전송이 실패하였습니다."),
	
	ValidateException("301", "검증오류가 발생하였습니다."),
	AttemptAuthenticationException("302", "인증오류가 발생하였습니다."),

	NotExistTokenException("601", "뿌리기 토큰정보가 존재하지 않습니다."),
	NotExistDistUserException("602", "뿌리기 사용자 정보가 존재하지 않습니다."),
	NotExistDistRoomException("603", "뿌리기 대화방 정보가 존재하지 않습니다."),
	AmountNotOverZeroException("604", "뿌리기 금액은 0원보다 커야 합니다. 뿌리기 금액:#{0}"),
	DistCountNotOverZeroException("605", "뿌리기 인원은 0보다 커야 합니다. 뿌리기 인원:#{0}"),
	NotEnoughAmountException("606", "뿌리기 금액은 뿌리기 인원보다 커야 합니다. 뿌리기 금액:#{0}, 뿌리기 인원:#{1}"),
	NotExistDistributeByTokenException("607", "해당 토큰에 대한 뿌리기정보가 존재하지 않습니다. 요청 토큰정보:#{0}"),
	NotReiveDistributeUserException("608", "자신이 뿌린 금액은 받을 수 없습니다. 사용자ID:#{0}"),
	NotEqualsRoomIDException("609", "뿌린이가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다. 뿌리기대화방ID:#{0}, 요청대화방ID:#{1}"),
	AlreadyReicevedUserException("610", "사용자가 이미 뿌리기 금액을 받았습니다. 뿌리기ID:#{0}, 사용자ID:#{1}"),
	ReceiveExpireTimeException("611", "받기 유효시간이 초과하였습니다. 받기유효시간:#{0}, 받기요청시간:#{1}"),
	AlreadyReicevedAllException("612", "이미 모든 인원이 받기가 완료 되었습니다."),
	DistributeQueryUserNotMatchException("613", "자신의 뿌리기 정보만 조회를 할 수 있습니다. 조회사용자ID:#{0}, 뿌리기사용자ID:#{1}"),
	QueryExpireTimeException("614", "조회하기 유효시간이 초과하였습니다. 조회하기유효시간:#{0}, 조회하기요청시간:#{1}"),
	NotExistApprRequestException("615", "존재하지 않는 전문형식입니다."),
	ManyTransactionException("616", "받기 사용자가 너무 많습니다. 다시 시도해 주세요."),


	DatabaseException("881", "데이터베이스 오류입니다."),
	DuplicationException("883", "데이터베이스에 중복된 정보가 있습니다."),
	
	ServiceException("900", "서비스 오류입니다."),
	SystemError("999", "시스템오류가 발생했습니다.");


	private String code;
    private String message;
    

    ErrorCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
    
	public String getCode() {		
		return code;
	}
	
	public String getMessage() {	
		return message;
	}
	
	public String getMessage(String[] objs) {
		String remixMessage = null;
		try {
			remixMessage = PatternMacherServiceImpl.getMachingMessage(this.message, objs);
		} catch (Exception ex) {
			remixMessage = this.message;
		}
		
		return remixMessage;
	}
	
	public static String getMessage(String code) {
		for(ErrorCodeEnum t: ErrorCodeEnum.values()) {
			if( t.getCode() == code ) {
				return t.getMessage();
			}
		}
		
		return "";
	}
	
	public static String getMessage(String code, String[] objs) {
		for(ErrorCodeEnum t: ErrorCodeEnum.values()) {
			if( t.getCode() == code ) {
				return t.getMessage(objs);
			}
		}
		
		return null;
	}

	
}