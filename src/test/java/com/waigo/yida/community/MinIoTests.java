package com.waigo.yida.community;

import com.waigo.yida.community.config.properties.MinioProperties;
import com.waigo.yida.community.util.MinioBucketUtil;
import com.waigo.yida.community.util.MinioObjectUtil;
import io.minio.ObjectWriteResponse;
import io.minio.http.Method;
import org.checkerframework.checker.units.qual.min;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * author waigo
 * create 2022-04-24 11:22
 */
@SpringBootTest
public class MinIoTests {
    @Autowired
    MinioObjectUtil minioObjectUtil;
    @Autowired
    MinioBucketUtil minioBucketUtil;
    @Autowired
    MinioProperties minioProperties;
    @Test
    public void testCreateFolder(){
        LocalDate now = LocalDate.now();
        int monthValue = now.getMonthValue();
        int year = now.getYear();
        ObjectWriteResponse resp = minioObjectUtil.createFolder(minioProperties.getVideoBucket(), year, monthValue);
        System.out.println(resp);
    }
    @Test
    public void testUploadFile(){
        System.out.println(minioObjectUtil.getPresignedObjectUrl(Method.PUT,minioProperties.getVideoBucket(),MinioObjectUtil.getYearMonthFolder()+"1.avi",null,TimeUnit.MINUTES,5));
        System.out.println();
    }
}
