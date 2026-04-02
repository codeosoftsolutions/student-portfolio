

package com.studenttap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    // ✅ Handle root
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    // ✅ Handle dynamic portfolio URLs
    @GetMapping("/{username}")
    public String portfolio() {
        return "forward:/index.html";
    }
}