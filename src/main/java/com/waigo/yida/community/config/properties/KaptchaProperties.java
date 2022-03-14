package com.waigo.yida.community.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * author waigo
 * create 2021-10-03 20:51
 */
@Configuration
@ConfigurationProperties(prefix = "security.captcha")
public class KaptchaProperties {
    private String type;
    private boolean enable;
    /**
     * 验证码的图片类型，jpg还是png
     */
    private String imgType;
    /**
     * 验证码的有效时间,单位 :分钟
     */
    private int ttl;

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
