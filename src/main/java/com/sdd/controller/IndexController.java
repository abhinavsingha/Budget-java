package com.sdd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class IndexController {

    private static String applicationName = "Budget Module";

    @GetMapping("/")
    public String login() {
        return applicationName;
    }



}
