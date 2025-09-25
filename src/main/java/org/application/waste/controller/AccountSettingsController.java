package org.application.waste.controller;


import jakarta.validation.Valid;
import org.application.waste.dto.AccountSettingsDto;
import org.application.waste.dto.BillingAddressDto;
import org.application.waste.dto.ChangePasswordDto;
import org.application.waste.entity.Profile;
import org.application.waste.entity.User;
import org.application.waste.security.CustomUserDetails;
import org.application.waste.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.Collator;
import java.util.*;

@Controller
@RequestMapping("/account/setting")
public class AccountSettingsController {

    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;


    public AccountSettingsController(ProfileService profileService, PasswordEncoder passwordEncoder) {
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
    }

    /* ---------- Pre-populare formulare ---------- */

    @ModelAttribute("accountSettings")
    public AccountSettingsDto accountSettings(@AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        Profile p = profileService.getOrCreateProfileFor(user);

        AccountSettingsDto dto = new AccountSettingsDto();
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setEmail(p.getEmail() != null ? p.getEmail() : user.getEmail());
        dto.setPhone(p.getPhone());
        dto.setAvatarUrl(p.getAvatarUrl());
        return dto;
    }

    @ModelAttribute("billingAddress")
    public BillingAddressDto billingAddress(@AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        Profile p = profileService.getOrCreateProfileFor(user);

        BillingAddressDto dto = new BillingAddressDto();
        dto.setBillingFirstName(p.getBillingFirstName());
        dto.setBillingLastName(p.getBillingLastName());
        dto.setCompanyName(p.getCompanyName());
        dto.setStreetAddress(p.getStreetAddress());
        dto.setCountry(p.getCountry());
        dto.setState(p.getState());
        dto.setBillingEmail(p.getBillingEmail());
        dto.setBillingPhone(p.getBillingPhone());
        return dto;
    }

    @ModelAttribute("changePassword")
    public ChangePasswordDto changePassword() {
        return new ChangePasswordDto();
    }

    @ModelAttribute("countries")
    public Map<String, String> countries() {
        Locale ro = Locale.forLanguageTag("ro");
        Collator collator = Collator.getInstance(ro);
        collator.setStrength(Collator.PRIMARY);

        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new Locale("", code))
                .sorted((a, b) -> collator.compare(a.getDisplayCountry(ro), b.getDisplayCountry(ro)))
                .collect(LinkedHashMap::new,
                        (m, loc) -> m.put(loc.getCountry(), loc.getDisplayCountry(ro)),
                        Map::putAll);
    }

    @ModelAttribute("avatarBust")
    public long avatarBust() {
        return System.currentTimeMillis();
    }

    /* ---------- GET ---------- */

    @GetMapping
    public String getAccountSettingPage(Model model) {
        model.addAttribute("page", "account-setting");
        return "account-setting";
    }


    @PostMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateAccountInfo(@AuthenticationPrincipal CustomUserDetails principal,
                                    @Valid @ModelAttribute("accountSettings") AccountSettingsDto accountDto,
                                    BindingResult result,
                                    @RequestParam(value = "avatar", required = false) MultipartFile ignoredFile,
                                    RedirectAttributes ra,
                                    Model model) {

        User user = principal.getUser();
        Profile current = profileService.getOrCreateProfileFor(user);

        accountDto.setAvatarUrl(current.getAvatarUrl());

        if (!result.hasErrors()) {
            profileService.updateAccountSettings(user, accountDto);
            ra.addFlashAttribute("successInfo", "Setările contului au fost actualizate cu succes.");
            return "redirect:/account/setting?t=" + System.currentTimeMillis();
        }

        model.addAttribute("page", "account-setting");
        return "account-setting";
    }

    /* ---------- UPDATE BILLING ---------- */

    @PostMapping("/billing")
    public String updateBilling(@AuthenticationPrincipal CustomUserDetails principal,
                                @Valid @ModelAttribute("billingAddress") BillingAddressDto billingDto,
                                BindingResult result,
                                Model model) {
        User user = principal.getUser();
        if (!result.hasErrors()) {
            try {
                profileService.updateBillingAddress(user, billingDto);
                model.addAttribute("successBilling", "Adresa de facturare a fost actualizată cu succes.");
            } catch (Exception ex) {
                result.reject("billingError", "A apărut o eroare neașteptată.");
            }
        }
        model.addAttribute("page", "account-setting");
        return "account-setting";
    }

    /* ---------- CHANGE PASSWORD ---------- */

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                                 @Valid @ModelAttribute("changePassword") ChangePasswordDto pwdDto,
                                 BindingResult result,
                                 Model model) {
        User user = principal.getUser();

        if (!Objects.equals(pwdDto.getNewPassword(), pwdDto.getConfirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "confirmNewPassword", "Parolele nu coincid");
        }

        String current = pwdDto.getCurrentPassword();
        if (current != null && !current.isBlank()) {
            if (!passwordEncoder.matches(current, user.getPassword())) {
                result.rejectValue("currentPassword", "currentPassword.incorrect", "Parola curentă este incorectă");
            }
        }

        if (!result.hasErrors()) {
            try {
                profileService.changePassword(user, pwdDto);
                model.addAttribute("successPassword", "Parola a fost schimbată cu succes.");
                model.addAttribute("changePassword", new ChangePasswordDto());
            } catch (IllegalArgumentException ex) {
                String msg = ex.getMessage() != null ? ex.getMessage() : "Eroare la schimbarea parolei";
                if (msg.contains("curent")) {
                    result.rejectValue("currentPassword", "currentPassword", msg);
                } else if (msg.contains("coincid")) {
                    result.rejectValue("confirmNewPassword", "confirmNewPassword", msg);
                } else if (msg.contains("cerinț")) {
                    result.rejectValue("newPassword", "newPassword", msg);
                } else {
                    result.reject("passwordError", msg);
                }
            } catch (Exception ex) {
                result.reject("passwordError", "A apărut o eroare neașteptată.");
            }
        }

        model.addAttribute("page", "account-setting");
        return "account-setting";
    }
}