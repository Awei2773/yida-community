package com.waigo.yida.community.entity;


/**
 * 上传文件成功之后返回的路径对象
 */
public class FileUploadResponse {
    private FileUploadResponse() {

    }

    private String url;

    private String objectName;

    private String preSignUrl;

    public static FileUploadResponse of(String endPoint, String bucket, String objectName, String preSignUrl) {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.url = endPoint + "/" + bucket + "/" + objectName;
        fileUploadResponse.objectName = objectName;
        fileUploadResponse.preSignUrl = preSignUrl;
        return fileUploadResponse;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getPreSignUrl() {
        return preSignUrl;
    }

    public void setPreSignUrl(String preSignUrl) {
        this.preSignUrl = preSignUrl;
    }
}