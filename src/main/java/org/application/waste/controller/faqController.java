package org.application.waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class faqController {

    @GetMapping("/faq")
    public String showFaqPage() {
        return "faq";
    }
}
