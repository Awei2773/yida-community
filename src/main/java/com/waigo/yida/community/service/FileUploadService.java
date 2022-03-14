package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Status;
import org.springframework.web.multipart.MultipartFile;

/**
 * author waigo
 * create 2021-10-04 19:57
 */
public interface FileUploadService {
    int MAX_FILE_SIZE = 1024*1024*15;//15M
    Status saveFiles(MultipartFile[] multipartFile, String[] fileNames, String savePath, int n);
    Status saveSingleFile(MultipartFile multipartFile, String fileName, String savePath);

}
