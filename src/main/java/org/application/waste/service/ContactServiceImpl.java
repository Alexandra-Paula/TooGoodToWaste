package org.application.waste.service;

import lombok.RequiredArgsConstructor;
import org.application.waste.dto.ContactFormDto;
import org.application.waste.entity.ContactMessage;
import org.application.waste.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMessageRepository repository;

    @Override
    public void handleContactForm(ContactFormDto dto) {
        ContactMessage m = new ContactMessage();
        m.setName(dto.getName().trim());
        m.setEmail(dto.getEmail().trim());
        m.setTitle(dto.getTitle().trim());
        m.setMessage(dto.getMessage().trim());
        repository.save(m);

    }
}
