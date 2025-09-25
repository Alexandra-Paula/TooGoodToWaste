package org.application.waste.service;

import org.application.waste.dto.AccountSettingsDto;
import org.application.waste.dto.BillingAddressDto;
import org.application.waste.dto.ChangePasswordDto;
import org.application.waste.entity.Profile;
import org.application.waste.entity.User;

public interface ProfileService {

    Profile getOrCreateProfileFor(User user);

    void updateAccountSettings(User user, AccountSettingsDto dto); // + sync în User.email
    void updateBillingAddress(User user, BillingAddressDto dto);
    void updateAvatarUrl(User user, String url);
    void changePassword(User user, ChangePasswordDto dto); // toate verificările + setare parolă

}