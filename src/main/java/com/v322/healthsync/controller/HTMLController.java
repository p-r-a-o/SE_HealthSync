package com.v322.healthsync.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HTMLController {

    // @GetMapping("")
    // public ModelAndView home() {
    //     ModelAndView mav=new ModelAndView("index");
    //     return mav;
    // }
    // @RequestMapping("/error")
    // public String error() {
    //     return "forward:/404.html";
    // }
    @RequestMapping("/")
    public String home() {
        System.err.println("test");
        return "forward:/index.html"; // Thymeleaf resolves this to src/main/resources/templates/index.html
    }

}