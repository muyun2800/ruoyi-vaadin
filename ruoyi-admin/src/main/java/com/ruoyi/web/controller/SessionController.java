package com.ruoyi.web.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SessionController
 *
 * @author 何德海
 * @version 2025/07/03 13:45
 **/
@RestController
@RequestMapping("api/session")
public class SessionController {


    @RequestMapping("put1")
    public void put1( String value, HttpSession session) {
        session.setAttribute("foo", value);
    }

    @RequestMapping("get1")
    public String get(HttpSession session) {
        String value = (String) session.getAttribute("foo");
        return value;
    }

}