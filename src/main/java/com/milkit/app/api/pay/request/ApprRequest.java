package com.milkit.app.api.pay.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@RequiredArgsConstructor
@ApiModel
public class ApprRequest {
    
    @ApiModelProperty(value="뿌리기를 요청한 계정")
    @JsonIgnore
    private String userID;

    @ApiModelProperty(value="뿌리기 대상 대화방 ID")
    @JsonIgnore
    private String roomID;

    
    public ApprRequest(String userID, String roomID) {
    	this.userID = userID;
    	this.roomID = roomID;
    }
}
