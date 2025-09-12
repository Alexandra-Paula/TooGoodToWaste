package org.application.waste.controller;

import jakarta.validation.Valid;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.waste.dto.UserRegisterDto;
import org.application.waste.entity.User;
import org.application.waste.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class RegisterController {
    @Autowired
    public UserServiceImpl userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserRegisterDto());
        model.addAttribute("page", "register");
        return "create-account";

    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("userDto") UserRegisterDto userRegisterDto,
                               BindingResult result,
                               Model model) {

        // verificare username și email folosind metodele noi
        if (userService.usernameExists(userRegisterDto.getUsername())) {
            result.rejectValue("username", null, "Username-ul este deja inregistrat");
            System.out.println("Eroare username adaugata: " + result.getFieldError("username"));
        }

        if (userService.emailExists(userRegisterDto.getEmail())) {
            result.rejectValue("email", null, "Email-ul este deja folosit");
            System.out.println("Eroare email adaugata: " + result.getFieldError("email"));
        }

        // verificare confirmPassword
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Parolele nu coincid");
        }

        System.out.println("Toate erorile: " + result.getAllErrors());

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", result.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("userDto", userRegisterDto);
            return "create-account"; // reafișează formularul cu erori
        }

        userService.saveUser(userRegisterDto);
        return "redirect:/index";
    }
}
