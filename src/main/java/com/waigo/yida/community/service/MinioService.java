package com.waigo.yida.community.service;

import com.waigo.yida.community.entity.FileUploadResponse;

/**
 * author waigo
 * create 2022-04-24 15:40
 */
public interface MinioService {
    /**
     * 获取文件上传的预签名url info
     * @param bucket
     * @param fileName
     * @return
     * @see com.waigo.yida.community.entity.FileUploadResponse
     */
    FileUploadResponse getFileUploadPolicy(String bucket, String fileName);
}
