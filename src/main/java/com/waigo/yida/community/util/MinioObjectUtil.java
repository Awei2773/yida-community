package com.waigo.yida.community.util;

import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * author waigo
 * create 2022-04-24 10:19
 */
@Component
public class MinioObjectUtil {
    public static final String FOLDER_SEPARATOR = "/";
    public static final Logger logger = LoggerFactory.getLogger(MinioObjectUtil.class);
    @Autowired
    MinioClient minioClient;

    /**
     * 获取 HTTP 方法、到期时间和自定义请求参数的对象的预签名 URL。
     * 适配restful规范，根据method的不同url有不同的作用
     * @param method
     * @param bucket
     * @param object
     * @param reqParams
     * @param timeUnit
     * @param time
     * @return
     */
    public String getPresignedObjectUrl(Method method, String bucket, String object, Map<String, String> reqParams,TimeUnit timeUnit,int time){
        if(reqParams==null){
            reqParams = new HashMap<>();
        }
        reqParams.put("response-content-type", "application/json");
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(bucket)
                            .object(object)
                            .expiry(time, timeUnit)
                            .extraQueryParams(reqParams)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("getPresignedObjectUrl执行失败！！！");
        return null;
    }
    public ObjectWriteResponse createFolder(String bucket,Object... folders){
        try {
            return minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucket).object(getFolder(folders)).stream(
                            new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("createFolder执行失败！！！");
        return null;
    }

    public static String getFolder(Object... folders){
        StringJoiner stringJoiner = new StringJoiner(FOLDER_SEPARATOR, "", FOLDER_SEPARATOR);
        for (Object folderPart : folders) {
            stringJoiner.add(folderPart.toString());
        }
        return stringJoiner.toString();
    }
    public static String getYearMonthFolder(){
        LocalDate now = LocalDate.now();
        int monthValue = now.getMonthValue();
        int year = now.getYear();
        return getFolder(year,monthValue);
    }
}
