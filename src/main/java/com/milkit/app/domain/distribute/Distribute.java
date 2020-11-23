package com.milkit.app.domain.distribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.milkit.app.common.DistributeSizeCommon;
import com.milkit.app.domain.distributedetail.DistributeDetail;
import com.milkit.app.util.DateUtil;
import com.milkit.app.util.StringUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "DISTRIBUTE")
@Entity
@ApiModel
public class Distribute {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
    @SequenceGenerator(
        name = "DISTRIBUTE_SEQ_GENERATOR",
        sequenceName="DISTRIBUTE_SEQ",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "DISTRIBUTE_SEQ_GENERATOR")
*/
    @Column(name = "ID")
    @ApiModelProperty(value="Distribute의 키ID")
    @JsonIgnore
    private Long id;

    @Column(name = "USER_ID")
    @ApiModelProperty(value="뿌리기를 요청한 계정")
    @JsonIgnore
    private String userID;

    @Column(name = "ROOM_ID")
    @ApiModelProperty(value="뿌리기 대상 대화방 ID")
    @JsonIgnore
    private String roomID;

    @Column(name = "AMOUNT")
    @ApiModelProperty(value="뿌리기금액")
    private Long amount;

    @Column(name = "DIST_COUNT")
    @ApiModelProperty(value="뿌리기 요청인원")
    @JsonIgnore
    private int distCount;

    @Column(name = "TOKEN", unique=true)
    @ApiModelProperty(value="뿌리기/받기에 사용되는 토큰")
    @JsonIgnore
    private String token;

        
    @Column(name = "DIST_TIME")
    @ApiModelProperty(value="사용자 등록시간")
    @JsonIgnore
    private Date distTime;    

    @Column(name = "RECEIVE_LIMIT_TIME")
    @ApiModelProperty(value="뿌리기 받기 유효시간")
    @JsonIgnore
    private Date receiveLimitTime;

    @Column(name = "QUERY_LIMIT_TIME")
    @ApiModelProperty(value="뿌리기정보 조회 유효시간")
    @JsonIgnore
    private Date queryLimitTime;

/*    
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "DIST_ID", updatable = false, insertable = false, nullable=true)
    @JsonManagedReference
    private List<DistributeDetail> distributeDetail;
*/

    @Transient
    @ApiModelProperty(value="받기 완료된 금액")
    private Long receiveAmount;

        
    @JsonProperty(value="distTime")
    @ApiModelProperty(value="뿌리기 시간")
    public String getDistTimeStr() {
        String timeStr = "";
        if(distTime != null) {
            timeStr = DateUtil.getFormatedTimeString(distTime, "yyyy-MM-dd HH:mm:ss");
        }
        return timeStr;
    }

    @Transient
    @ApiModelProperty(value="뿌리기 받기 완료 상세정보")
    private List<DistributeDetail> detail;
    
  
	public void setReceiveAmount(List<DistributeDetail> list) {
        if(list != null) {
			receiveAmount = list.stream().mapToLong(x -> x.getAmount()).sum();
		} else {
			receiveAmount = 0l;
		}
    }

	@Override  
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this, ToStringStyle.SHORT_PREFIX_STYLE
		);
	}


}
