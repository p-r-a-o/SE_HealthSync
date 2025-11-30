package com.v322.healthsync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IndexController {

    // @GetMapping("")
    // public ModelAndView home() {
    //     ModelAndView mav=new ModelAndView("index");
    //     return mav;
    // }
    

    @GetMapping("/ping")
    public String ping() {
        return "PINGED Server";
    }
    
    @GetMapping("/api/**")
    public String _home() {
        return "API";
    }

}