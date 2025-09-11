package org.application.waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @GetMapping({"/about", "/despre"})
    public String about() {
        return "about";
    }
}
