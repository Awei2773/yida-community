package com.waigo.yida.community.config.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * author waigo
 * create 2021-10-04 19:48
 */
@Component
public class CommunityProperties {
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${server.host}")
    private String host;
    @Value("${server.protocol}")
    private String protocol;
    //文件存储路径，不能加最后的/
    @Value("${server.file.save.path}")
    private String savePath;

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String SERVER_CONTEXT_PATH;
    @PostConstruct
    public void setServerCtxPath(){
        SERVER_CONTEXT_PATH = protocol + "://" + host+":"+port+"/"+(StringUtils.isBlank(contextPath)?"":contextPath + "/");
    }
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
