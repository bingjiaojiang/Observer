package com.future.observermonitor.service;

import com.future.observercommon.vo.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("ysopen-api")
@Service
public interface YSOpenService {

    @GetMapping("/access-token")
    ResponseResult getMonitorToken() throws Exception;
}
