package com.milkit.app.api.pay.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@RequiredArgsConstructor
@ApiModel
public class ReceiveRequest extends ApprRequest {
    
    @ApiModelProperty(value="뿌린돈 받기에 사용되는 토큰")
    private String token;
    
}
