package org.application.waste.service;

import org.application.waste.dto.ChatDto;

import java.util.List;

public interface AiService {
    //    String callAiModel(String title, String description);
    String getResponseAction(String message, Long userId);

    void deleteChatHistoryAction(Long userId);

    List<ChatDto> retrieveUserChatsAction(Long userId);
}
