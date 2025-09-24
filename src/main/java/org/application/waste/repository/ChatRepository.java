package org.application.waste.repository;

import org.application.waste.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    // Metodă care aduce toate mesajele după userId
    List<Chat> findByUserId(Long userId);

    // Dacă vrei și ordonare
    List<Chat> findAllByUserIdOrderByResponseDateAsc(Long userId);

    // Dacă vrei să ștergi direct toate mesajele după userId
    void deleteAllByUserId(Long userId);


}