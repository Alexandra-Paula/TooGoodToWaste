package org.application.waste.controller;

import org.application.waste.entity.User;
import org.application.waste.exceptions.FileTooLargeException;
import org.application.waste.exceptions.InvalidFileException;
import org.application.waste.exceptions.NotFoundException;
import org.application.waste.service.ProductLinkService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {
    private final ProductLinkService productLinkService;
    private final UserService userService;

    public ProductController(ProductLinkService productLinkService, UserService userService) {
        this.productLinkService = productLinkService;
        this.userService = userService;
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String existingLink = null;
        if (user != null && user.getProductLink() != null) {
            existingLink = user.getProductLink().getOriginalLink();
        }

        model.addAttribute("page", "upload");
        model.addAttribute("existingLink", existingLink);

        return "products-import";
    }

    @PostMapping("/upload/link")
    public String handleFileUploadFromLink(@RequestParam("fileUrl") String fileUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user == null || user.getId() == null) {
            return "redirect:/upload?error=User not logged in";
        }

        try {
            productLinkService.saveProductsFromLink(fileUrl, user.getId());
            return "redirect:/upload?success=File downloaded and saved successfully";
        } catch (FileTooLargeException | InvalidFileException | NotFoundException e) {
            return "redirect:/upload?error=" + e.getMessage();
        }
    }
}