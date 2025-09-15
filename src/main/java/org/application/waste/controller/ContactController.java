package org.application.waste.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.application.waste.dto.ContactFormDto;
import org.application.waste.entity.User;
import org.application.waste.service.ContactService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final UserService userService;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        if (!model.containsAttribute("contactDto")) {
            model.addAttribute("contactDto", new ContactFormDto());
        }
        model.addAttribute("page", "contact");
        return "contact"; // caută templates/contact.html
    }

    @PostMapping("/contact")
    public String submitContact(
            @Valid @ModelAttribute("contactDto") ContactFormDto dto,
            BindingResult result,
            RedirectAttributes ra
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (result.hasErrors()) {

            ra.addFlashAttribute("org.springframework.validation.BindingResult.contactDto", result);
            ra.addFlashAttribute("contactDto", dto);
            return "redirect:/contact";
        }

        contactService.handleContactForm(dto);
        ra.addFlashAttribute("successMessage", "Mulțumim! Mesajul tău a fost trimis.");
        return "redirect:/contact?success";
    }

}
