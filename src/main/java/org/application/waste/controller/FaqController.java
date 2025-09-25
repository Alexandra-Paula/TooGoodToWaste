package org.application.waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaqController {

    @GetMapping("/faq")
    public String showFaqPage(Model model) {
        model.addAttribute("page", "faq");
        return "faq";
    }
}