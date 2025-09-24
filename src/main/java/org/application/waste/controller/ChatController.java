package org.application.waste.controller;

import org.application.waste.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.application.waste.entity.User;
import org.application.waste.service.AiService;
import org.application.waste.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.application.waste.repository.UserRepository;



import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {

    private final AiService chatAiService;
    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    public ChatController(AiService chatAiService, UserService userService) {

        this.chatAiService = chatAiService;
        this.userService = userService;

    }

    @GetMapping("/chat")
    public String showChat(Model model, Authentication authentication) {
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            currentUser = userService.findByEmail(principal)
                    .orElseGet(() -> userService.findByUsername(principal).orElse(null));
        }

        if (currentUser != null) {
            var chats = chatAiService.retrieveUserChatsAction(currentUser.getId());
            model.addAttribute("allChats", chats);
            model.addAttribute("userId", currentUser.getId());
        } else {
            model.addAttribute("userId", null);
        }

        return "chat";
    }
    private Long generateGuestId() {
        // Generăm un ID unic pe baza timestamp-ului curent
        return System.currentTimeMillis();
    }
    @PostMapping("/chat/respond")
    public ResponseEntity<Map<String, Object>> respond(@RequestBody Map<String, Object> payload) {
        String message = (String) payload.get("message");
        Object userIdObj = payload.get("userId");

        Long userId = null;

        // Dacă vine userId din frontend (numeric sau guest-xxx)
        if (userIdObj != null) {
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else {
                try {
                    String str = userIdObj.toString();
                    if (!str.startsWith("guest-")) {
                        userId = Long.parseLong(str);
                        if (!userRepository.existsById(userId)) {
                            userId = null; // fallback guest
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Dacă user-ul e logat și userId e null
        if (userId == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() &&
                    !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
                CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
                userId = principal.getId(); // trebuie să existe getId() în CustomUserDetails
            }
        }

        // Apelează service-ul, service-ul va salva în DB
        String aiResponse = chatAiService.getResponseAction(message, userId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("responseText", aiResponse);

        return ResponseEntity.ok(responseMap);
    }



    @PostMapping("/chat/delete")
    public String deleteChatHistory(Authentication authentication) {
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(String.valueOf(authentication.getPrincipal()))) {

            String principal = authentication.getName();
            currentUser = userService.findByEmail(principal).orElseGet(() ->
                    userService.findByUsername(principal).orElse(null)
            );
        }

        if (currentUser != null) {
            chatAiService.deleteChatHistoryAction(currentUser.getId());
        }

        return "redirect:/chat?success=true";
    }

}

