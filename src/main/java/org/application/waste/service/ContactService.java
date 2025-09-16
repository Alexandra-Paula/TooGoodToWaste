package org.application.waste.service;

import org.application.waste.dto.ContactFormDto;
import org.application.waste.entity.User;

public interface ContactService {
    void handleContactForm(ContactFormDto dto, User currentUser);
}
