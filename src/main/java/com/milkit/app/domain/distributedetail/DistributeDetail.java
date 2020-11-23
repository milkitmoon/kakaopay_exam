package com.milkit.app.domain.distributedetail;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "DISTRIBUTE_DETAIL",
uniqueConstraints={
    @UniqueConstraint(
        columnNames={"DIST_ID","USER_ID"}
    )
})
@Entity
@ApiModel
public class DistributeDetail {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(value="DISTRIBUTE_DETAIL의 키ID")
    @JsonIgnore
    private Long id;

    @Column(name = "DIST_ID")
    @ApiModelProperty(value="뿌리기ID")
    @JsonIgnore
    private Long distID;

    @Column(name = "USER_ID")
    @ApiModelProperty(value="뿌리기를 받은 계정")
    private String userID;
    
    @Column(name = "AMOUNT")
    @ApiModelProperty(value="분배 뿌리기금액")
    private Long amount;

    @Column(name = "RECEIVE_TIME")
    @ApiModelProperty(value="뿌리기 받은시간")
    @JsonIgnore
    private Date receiveTime;
    
    @Column(name = "RECEIVE_YN")
    @ApiModelProperty(value="뿌리기 받은여부")
    @JsonIgnore
    private String receiveYN;

    
	
	@Override  
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this, ToStringStyle.SHORT_PREFIX_STYLE
		);
	}
}
