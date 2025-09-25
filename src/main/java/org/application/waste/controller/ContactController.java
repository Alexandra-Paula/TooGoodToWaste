package org.application.waste.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.application.waste.dto.ContactFormDto;
import org.application.waste.entity.User;
import org.application.waste.service.ContactService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
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
        return "contact";
    }

    @PostMapping("/contact/submit")
    public String submitContact(
            @Valid @ModelAttribute("contactDto") ContactFormDto dto,
            BindingResult result,
            RedirectAttributes ra,
            Authentication authentication
    ) {
        if (result.hasErrors()) {
            String errorMsg;

            if (result.hasFieldErrors("email")) {
                errorMsg = result.getFieldError("email").getDefaultMessage();
            } else if (result.hasFieldErrors("message")) {
                errorMsg = result.getFieldError("message").getDefaultMessage();
            } else {
                errorMsg = "Date invalide sau incomplete. Repetați formularul!";
            }

            ra.addAttribute("error", errorMsg);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.contactDto", result);
            ra.addFlashAttribute("contactDto", dto);
            return "redirect:/contact";
        }

        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName(); // de obicei username
            currentUser = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        contactService.handleContactForm(dto, currentUser);
        ra.addFlashAttribute("successMessage", "Mulțumim! Mesajul tău a fost trimis.");
        return "redirect:/contact?success";
    }
}