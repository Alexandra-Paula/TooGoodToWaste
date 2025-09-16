package org.application.waste.repository;

import java.util.List;
import org.application.waste.entity.ContactMessage;
import org.application.waste.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByUser(User user);
}
