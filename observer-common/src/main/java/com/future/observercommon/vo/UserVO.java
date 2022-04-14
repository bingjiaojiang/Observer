package com.future.observercommon.vo;

import com.future.observercommon.dto.CompanyDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("用户VO")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserVO {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("用户头像")
    private String headImg;

    @ApiModelProperty("所在企业的信息")
    private CompanyDTO companyDTO;
}
