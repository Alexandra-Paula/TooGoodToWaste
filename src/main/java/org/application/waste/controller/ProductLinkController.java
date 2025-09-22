package org.application.waste.controller;

import org.application.waste.entity.User;
import org.application.waste.exceptions.FileTooLargeException;
import org.application.waste.exceptions.InvalidFileException;
import org.application.waste.exceptions.NotFoundException;
import org.application.waste.service.ProductLinkService;
import org.application.waste.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductLinkController {
    private final ProductLinkService productLinkService;
    private final UserService userService;

    public ProductLinkController(ProductLinkService productLinkService, UserService userService) {
        this.productLinkService = productLinkService;
        this.userService = userService;
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model, Authentication authentication) {
        User user = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            user = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        if (user == null || user.getId() == null) {
            return "redirect:/upload?error=Utilizator neautentificat";
        }

        String existingLink = null;
        if (user.getProductLink() != null) {
            existingLink = user.getProductLink().getOriginalLink();
        }

        model.addAttribute("page", "upload");
        model.addAttribute("existingLink", existingLink);

        return "products-import";
    }

    @PostMapping("/upload/link")
    public String handleFileUploadFromLink(@RequestParam("fileUrl") String fileUrl, Authentication authentication) {
        User user = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            user = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        if (user == null || user.getId() == null) {
            return "redirect:/upload?error=Utilizator neautentificat";
        }

        try {
            productLinkService.saveProductsFromLink(fileUrl, user.getId());
            return "redirect:/upload?success=Fisierul a fost descarcat si salvat cu succes";
        } catch (FileTooLargeException | InvalidFileException | NotFoundException e) {
            return "redirect:/upload?error=A aparut o eroare la incarcarea fisierului";
        }
    }
}