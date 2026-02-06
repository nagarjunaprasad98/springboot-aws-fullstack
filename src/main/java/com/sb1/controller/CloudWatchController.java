package com.sb1.controller;

import com.sb1.service.CloudWatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloudwatch")
public class CloudWatchController {

    @Autowired
    private CloudWatchService cwService;

    @PostMapping("/publish")
    public ResponseEntity<String> logMessage(@RequestBody String message) {

        cwService.logMessage(message);

        return ResponseEntity.ok("Message logged to CloudWatch");
    }
}
