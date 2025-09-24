package org.application.waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {
        @GetMapping("/privacyPolicy")
        public String showPrivacyPolicy() {
            return "privacyPolicy";
        }
}
