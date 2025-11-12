package com.stepfrontier.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/ping")
    public String ping() {
        System.out.println("✅ /api/v1/ping endpoint was called");
        return "pong";
    }
}
