package com.future.observermonitor.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("萤石开放平台的密钥VO")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SecretVO {

    @ApiModelProperty("AppKey")
    private String appKey;

    @ApiModelProperty("AppSecret")
    private String appSecret;

    @ApiModelProperty("AccessToken")
    private String accessToken;
}
