package com.future.observercommon.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("SecretDTO")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SecretDTO {

    @ApiModelProperty("萤石开放平台的AppKey")
    private String appKey;

    @ApiModelProperty("萤石开放平台的AppSecret")
    private String appSecret;

    @ApiModelProperty("萤石开放平台的AccessToken")
    private String accessToken;
}
