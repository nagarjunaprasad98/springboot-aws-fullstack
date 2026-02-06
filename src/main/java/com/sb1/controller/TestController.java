package com.sb1.controller;

import com.sb1.service.CloudWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private CloudWatchService cwService;

    @GetMapping("/")
    public String getMessage(){
        cwService.logMessage("DashBoard Called");
        return "Hello Welcome to Nagarjuna's Application!";

    }
}
