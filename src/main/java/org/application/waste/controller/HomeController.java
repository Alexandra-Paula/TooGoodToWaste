package org.application.waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("page", "home");
        return "index";
    }
}
