package com.future.observercommon.config;

import com.future.observercommon.dto.ImgBasePath;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public ImgBasePath imgBasePath() {
        ImgBasePath imgBasePath = new ImgBasePath();

        imgBasePath.setImgPath("E:/root/software/observer/imgs/");

        imgBasePath.setMonitorPath(imgBasePath.getImgPath() + "monitor/");
        imgBasePath.setPublicMonitorPath(imgBasePath.getMonitorPath() + "public/");
        imgBasePath.setDrivingMonitorPath(imgBasePath.getImgPath() + "driving/");

        imgBasePath.setCompanyPath(imgBasePath.getImgPath() + "commpany/");
        imgBasePath.setCompanyLicensePath(imgBasePath.getCompanyPath() + "license/");

        imgBasePath.setUserPath(imgBasePath.getImgPath() + "user/");
        imgBasePath.setUserHeadImgPath(imgBasePath.getUserPath() + "head/");

        return imgBasePath;
    }
}
