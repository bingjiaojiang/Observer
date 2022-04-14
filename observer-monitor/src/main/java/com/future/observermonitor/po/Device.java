package com.future.observermonitor.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("monitor_device")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Device {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String appKey;

    private String appSecret;

    private String accessToken;

    private String name;

    private String deviceSerial;

    private String channelNo;

    private Integer userId;
}
