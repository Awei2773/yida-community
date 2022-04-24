package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.config.properties.MinioProperties;
import com.waigo.yida.community.entity.FileUploadResponse;
import com.waigo.yida.community.service.MinioService;
import com.waigo.yida.community.util.MinioBucketUtil;
import com.waigo.yida.community.util.MinioObjectUtil;
import com.waigo.yida.community.util.RandomCodeUtil;
import com.waigo.yida.community.util.SecurityUtil;
import io.minio.ListObjectsArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * author waigo
 * create 2022-04-24 15:42
 */
@Service
public class MinioServiceImpl implements MinioService {
    public static final int MAX_FILE_NAME_LENGTH = 40;
    @Autowired
    MinioObjectUtil minioObjectUtil;
    @Autowired
    MinioBucketUtil minioBucketUtil;
    @Autowired
    MinioProperties minioProperties;

    @Override
    public FileUploadResponse getFileUploadPolicy(String bucket, String fileName) {
        //1.判断bucket是否存在,不存在则创建
        if (!minioBucketUtil.bucketExists(bucket)) {
            minioBucketUtil.makeBucket(bucket);
        }
        //TODO:手动设置成可读可写
        //2.文件路径的设置
        //fileName -> a.jpg
        int dotIndex = fileName.lastIndexOf(".");
        String suffix = fileName.substring(dotIndex);
        if (fileName.length() - suffix.length() > MAX_FILE_NAME_LENGTH) {
            fileName = fileName.substring(0, MAX_FILE_NAME_LENGTH);
        }
        fileName = fileName + RandomCodeUtil.getRandomCode(7) + suffix;
        String objName = MinioObjectUtil.getYearMonthFolder() + fileName;
        String presignedObjectUrl = minioObjectUtil.getPresignedObjectUrl(Method.PUT, bucket, objName, null, TimeUnit.MINUTES, 5);
        if (presignedObjectUrl == null) {
            return null;
        }
        return FileUploadResponse.of(minioProperties.getEndpoint(), objName, presignedObjectUrl);
    }
}
