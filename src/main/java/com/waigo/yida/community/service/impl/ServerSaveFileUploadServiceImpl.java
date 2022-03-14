package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * author waigo
 * create 2021-10-04 20:04
 */

/**
 * 服务端存储方式
 */
@Configuration
@ConditionalOnProperty(prefix = "file.upload",name = "type",havingValue = "serverSide")
public class ServerSaveFileUploadServiceImpl implements FileUploadService {
    private  static final Logger LOGGER = LoggerFactory.getLogger(ServerSaveFileUploadServiceImpl.class);
    @Override
    public Status saveFiles(MultipartFile[] multipartFiles, String[] fileNames, String savePath, int n) {
        Status success = Status.success();
        for(int i = 0;i<n;i++){
            Status status = saveSingleFile(multipartFiles[i],fileNames[i],savePath);
            if(!status.isSuccess()){
                return status;
            }
            success.addAttribute(fileNames[i],status.get(fileNames[i]));
        }
        return success;
    }

    @Override
    public Status saveSingleFile(MultipartFile multipartFile, String fileName, String savePath) {
        if(multipartFile.getSize()>MAX_FILE_SIZE){
            String err = "文件不能超过15M!!!";
            LOGGER.error(err);
            Status failure = Status.failure();
            failure.addAttribute("error",err);
            return failure;
        }
        File folder = new File(savePath);
        if(!folder.exists()||!folder.isDirectory()){
            String err = "文件夹不存在!!!";
            LOGGER.error(err);
            Status failure = Status.failure();
            failure.addAttribute("error",err);
            return failure;
        }
        String path = savePath+"/"+fileName;
        File file = new File(path);
        Status status;
        try {
            multipartFile.transferTo(file);
            status = Status.success();
        } catch (IOException e) {
            LOGGER.error("文件存储失败：{}",e.getMessage());
            status = Status.failure();
            status.addAttribute("error","文件存储失败，服务端错误");
        }
        return status;
    }
}
