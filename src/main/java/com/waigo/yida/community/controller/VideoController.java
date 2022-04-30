package com.waigo.yida.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author waigo
 * create 2022-04-25 11:42
 */
@Controller
@RequestMapping("/video")
public class VideoController {
    @GetMapping("/upload-detail.html")
    public String getUploadDetailPage(){
        return "/site/upload-detail";
    }
}
