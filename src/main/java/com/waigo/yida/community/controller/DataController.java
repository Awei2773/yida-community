package com.waigo.yida.community.controller;

import com.waigo.yida.community.exception.AjaxException;
import com.waigo.yida.community.service.DataService;
import com.waigo.yida.community.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * author waigo
 * create 2022-05-01 23:05
 */
@Controller
@RequestMapping("/data")
public class DataController {
    @Autowired
    DataService dataService;
    @GetMapping("/data.html")
    public String getDataPage(){
        return "/site/admin/data";
    }
    @GetMapping("/segmentUV")
    @ResponseBody
    public R getSegmentUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,@DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        if(start.after(end)){
            throw new AjaxException(400,R.create(400,"开始时间需要小于等于结束时间"));
        }
        return R.create(200,"uv计算成功").addAttribute("uvResult",dataService.caculateSegmentUV(start, end));
    }
    @GetMapping("/segmentDAU")
    @ResponseBody
    public R getSegmentDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,@DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        if(start.after(end)){
            throw new AjaxException(400,R.create(400,"开始时间需要小于等于结束时间"));
        }
        return R.create(200,"dau计算成功").addAttribute("dauResult",dataService.caculateSegmentDAU(start, end));
    }
}
