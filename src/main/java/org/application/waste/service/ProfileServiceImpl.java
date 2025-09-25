package org.application.waste.service;

import org.application.waste.dto.AccountSettingsDto;
import org.application.waste.dto.BillingAddressDto;
import org.application.waste.dto.ChangePasswordDto;
import org.application.waste.entity.Profile;
import org.application.waste.entity.User;
import org.application.waste.repository.ProfileRepository;
import org.application.waste.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(ProfileRepository profileRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Profile getOrCreateProfileFor(User user) {
        return profileRepository.findByUserId(user.getId()).orElseGet(() -> {
            Profile p = new Profile();
            p.setUser(user);
            // inițial putem copia email/phone din user (dacă vrei)
            p.setEmail(user.getEmail());
            return profileRepository.save(p);
        });
    }

    @Override
    public void updateAccountSettings(User user, AccountSettingsDto dto) {
        Profile profile = getOrCreateProfileFor(user);

        // Validări simple (anotările @Valid se fac în Controller; aici doar sanity)
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("Emailul nu poate fi gol");
        }

        // Dacă s-a schimbat emailul din Account Settings, sincronizăm și în User,
        // DAR numai dacă nu aparține altui user.
        final String newEmail = dto.getEmail().trim();
        if (!newEmail.equalsIgnoreCase(user.getEmail())) {
            userRepository.findByEmail(newEmail).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("Email-ul este deja folosit de un alt cont");
                }
            });
            user.setEmail(newEmail);
            userRepository.save(user);
        }

        // Actualizăm profilul
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(newEmail);
        profile.setPhone(dto.getPhone());
        profile.setAvatarUrl(dto.getAvatarUrl());

        profileRepository.save(profile);
    }
    @Override
    public void updateAvatarUrl(User user, String url) {
        Profile p = getOrCreateProfileFor(user);
        p.setAvatarUrl(url);
        profileRepository.save(p);
    }

    @Override
    public void updateBillingAddress(User user, BillingAddressDto dto) {
        Profile profile = getOrCreateProfileFor(user);

        profile.setBillingFirstName(dto.getBillingFirstName());
        profile.setBillingLastName(dto.getBillingLastName());
        profile.setCompanyName(dto.getCompanyName());
        profile.setStreetAddress(dto.getStreetAddress());
        profile.setCountry(dto.getCountry());
        profile.setState(dto.getState());
        profile.setBillingEmail(dto.getBillingEmail());
        profile.setBillingPhone(dto.getBillingPhone());

        profileRepository.save(profile);
    }

    @Override
    public void changePassword(User user, ChangePasswordDto dto) {
        // 1) Câmpuri goale – sunt validate și cu @Valid, dar mai verificăm:
        if (!StringUtils.hasText(dto.getCurrentPassword())) {
            throw new IllegalArgumentException("Parola curentă nu poate fi goală");
        }
        if (!StringUtils.hasText(dto.getNewPassword())) {
            throw new IllegalArgumentException("Parola nouă nu poate fi goală");
        }
        if (!StringUtils.hasText(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Confirmarea parolei nu poate fi goală");
        }

        // 2) Confirmare
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Parola nouă și confirmarea ei nu coincid");
        }

        // 3) Parola curentă trebuie să fie corectă
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Parola curentă este incorectă");
        }

        // 4) Parola nouă NU trebuie să fie identică cu parola curentă
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Parola curentă nu trebuie să coincidă cu parola nouă");
        }

        // 5) Regex (deja în @Pattern la DTO; aici e fallback în caz că a venit fără @Valid)
        if (!dto.getNewPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#\\$%&'0*+,-/:<=>?@^_]).{8,}$")) {
            throw new IllegalArgumentException(
                    "Parola nouă nu respectă cerințele: minim 8 caractere, literă mare, literă mică, cifră, caracter special"
            );
        }

        // OK – setăm parola nouă
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
