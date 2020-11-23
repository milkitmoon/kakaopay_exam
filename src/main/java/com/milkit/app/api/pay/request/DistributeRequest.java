package com.milkit.app.api.pay.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@RequiredArgsConstructor
@ApiModel
public class DistributeRequest extends ApprRequest {
    
    @ApiModelProperty(value="뿌리기금액")
    private long amount;

    @ApiModelProperty(value="뿌리기 요청인원")
    private int distCount;
    
}
