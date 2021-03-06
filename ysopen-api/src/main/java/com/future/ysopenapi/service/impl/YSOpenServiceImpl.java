package com.future.ysopenapi.service.impl;

import com.future.observercommon.dto.DeviceDTO;
import com.future.observercommon.dto.SecretDTO;
import com.future.observercommon.util.HttpUtil;
import com.future.observercommon.util.JacksonUtil;
import com.future.ysopenapi.dto.YSOpenRequestInfo;
import com.future.ysopenapi.service.YSOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YSOpenServiceImpl implements YSOpenService {

    @Autowired
    private YSOpenRequestInfo ysOpenRequestInfo;

    @Override
    public String getAccessToken(SecretDTO secretDTO) throws Exception {
        /*
         * 请求地址和请求参数
         */
        // 请求地址
        String url = ysOpenRequestInfo.getUrlOfAccessToken();
        // 拼接请求参数
        String params = String.format(
                ysOpenRequestInfo.getParamsOfAccessToken(),
                secretDTO.getAppKey(),
                secretDTO.getAppSecret()
        );

        /*
         * 发送post请求，获取响应结果
         * 返回AccessToken
         */
        String result = HttpUtil.post(url, params);
        return JacksonUtil.jsonNodeOf(result, "data", "accessToken").asText();
    }

    @Override
    public String capture(DeviceDTO deviceDTO) throws Exception {
        /*
         * 请求地址和请求参数
         */
        // 请求地址
        String url = ysOpenRequestInfo.getUrlOfCapture();
        // 拼接请求参数
        String params = String.format(
                ysOpenRequestInfo.getParamsOfCapture(),
                deviceDTO.getAccessToken(),
                deviceDTO.getDeviceSerial(),
                deviceDTO.getChannelNo()
        );

        // 发送post请求，获取响应结果并返回
        return HttpUtil.post(url, params);
    }

    @Override
    public String getDeviceInfo(DeviceDTO deviceDTO) throws Exception {
        /*
         * 请求地址和请求参数
         */
        // 请求地址
        String url = ysOpenRequestInfo.getUrlOfDeviceInfo();
        // 拼接请求参数
        String params = String.format(
                ysOpenRequestInfo.getParamsOfDeviceInfo(),
                deviceDTO.getAccessToken(),
                deviceDTO.getDeviceSerial()
        );

        // 发送post请求，获取响应结果并返回
        return HttpUtil.post(url, params);
    }
}
