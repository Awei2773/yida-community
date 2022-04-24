package com.waigo.yida.community.util;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author waigo
 * create 2022-04-24 10:21
 */
@Component
public class MinioBucketUtil {
    public static final Logger logger = LoggerFactory.getLogger(MinioBucketUtil.class);
    @Autowired
    MinioClient minioClient;

    public boolean bucketExists(String bucket) {
        boolean found = false;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("bucketExists执行失败！！！");
        return found;
    }

    public void makeBucket(String bucket) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("makeBucket执行失败！！！");
    }

}
