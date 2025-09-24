package org.application.waste.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.application.waste.dto.ChatDto;
import org.application.waste.entity.Chat;
import org.application.waste.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String DEEPSEEK_API_KEY = "DEEPSEEK_API_KEY"; // pune aici cheia ta
    private static final int MAX_WORDS = 500;
    private static final Path ASSISTANT_CONTEXT_FILE = Path.of("src/main/resources/chat-data/assistant_context.txt");
    private static final Path PRODUCTS_FILE = Path.of("src/main/resources/chat-data/products.txt");

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @PostConstruct
    @Transactional
    public void init() {
        populateProductsFile();
    }

    // Populează products.txt cu produse fără duplicat
    public void populateProductsFile() {
        try {
            String existingContent = "";
            Set<String> existingProductNames = new HashSet<>();
            if (Files.exists(PRODUCTS_FILE)) {
                List<String> existingLines = Files.readAllLines(PRODUCTS_FILE, StandardCharsets.UTF_8);
                for (String line : existingLines) {
                    if (line.startsWith("- ")) {
                        String name = line.substring(2).split(" \\(")[0].trim();
                        existingProductNames.add(name);
                    }
                }
                existingContent = String.join("\n", existingLines) + "\n";
            }

            List<Map<String, Object>> products = productService.getProductsForAssistant();
            StringBuilder sb = new StringBuilder(existingContent);
            boolean anyNewProduct = false;

            for (Map<String, Object> p : products) {
                String name = p.get("name").toString();
                if (!existingProductNames.contains(name)) {
                    anyNewProduct = true;
                    sb.append("- ").append(name)
                            .append(" (").append(p.get("category")).append(") – ")
                            .append(p.get("finalPrice")).append(" lei, ")
                            .append("Reducere: ").append(p.get("discount")).append(", ")
                            .append("Cantitate: ").append(p.get("quantity")).append(", ")
                            .append("Calitate: ").append(p.get("quality"))
                            .append("\n  Descriere: ").append(p.get("description"))
                            .append(", Pick-up: ").append(p.get("pickupAddress"))
                            .append("\n\n");
                }
            }

            if (anyNewProduct) {
                Files.writeString(PRODUCTS_FILE, sb.toString(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Produse noi adaugate in products.txt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Încarcă contextul pentru AI (doar reguli / ton)
    private String loadDomainContext() {
        try {
            String context = Files.readString(ASSISTANT_CONTEXT_FILE, StandardCharsets.UTF_8);
            System.out.println("===== DomainContext Loaded for AI =====");
            System.out.println(context);
            System.out.println("======================================");
            return context;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ești un asistent inteligent pentru utilizatorii aplicației TooGoodToWaste din Republica Moldova...";
        }
    }

    @Override
    public String getResponseAction(String message, Long userId) {
        if (message == null || message.trim().isEmpty()) return "Message cannot be empty!";
        int wordCount = message.trim().split("\\s+").length;
        if (wordCount > MAX_WORDS) return "Message too long. Please limit to " + MAX_WORDS + " words!";

        String domainContext = loadDomainContext();

        try {
            ObjectMapper mapper = new ObjectMapper();

            StringBuilder userMessageBuilder = new StringBuilder(message);
            List<Map<String,Object>> products = productService.getProductsForAssistant();
            if (!products.isEmpty()) {
                userMessageBuilder.append("\nProduse disponibile:\n");
                for (Map<String,Object> p : products) {
                    userMessageBuilder.append("- ").append(p.get("name"))
                            .append(" (").append(p.get("category")).append(") – ")
                            .append(p.get("finalPrice")).append(" lei, ")
                            .append("Reducere: ").append(p.get("discount")).append(", ")
                            .append("Cantitate: ").append(p.get("quantity")).append(", ")
                            .append("\n  Descriere: ").append(p.get("description"))
                            .append(", Pick-up: ").append(p.get("pickupAddress"))
                            .append("\n\n");
                }
            }
            String payload = mapper.writeValueAsString(new Object() {
                public final String model = "deepseek-chat";
                public final Object[] messages = new Object[]{
                        new Object() { public final String role = "system"; public final String content = domainContext; },
                        new Object() { public final String role = "user"; public final String content = userMessageBuilder.toString(); }
                };
                public final boolean stream = false;
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + DEEPSEEK_API_KEY);

            HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    DEEPSEEK_API_URL, HttpMethod.POST, requestEntity, String.class
            );

            String responseString = responseEntity.getBody();
            JsonNode jsonNode = mapper.readTree(responseString);

            JsonNode choices = jsonNode.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstMessage = choices.get(0).path("message");
                if (!firstMessage.isMissingNode()) {
                    String responseText = firstMessage.path("content").asText();
                    if (responseText == null || responseText.trim().isEmpty()) {
                        return "Empty response from AI.";
                    }

                    String trimmedResponse = responseText.trim();

                    Chat chatEntry = new Chat();
                    chatEntry.setUserId(userId);
                    chatEntry.setPrompt(message);
                    chatEntry.setMessage(trimmedResponse);
                    chatEntry.setResponseDate(LocalDateTime.now());
                    chatRepository.save(chatEntry);

                    return trimmedResponse;
                }
            }

            return "Unexpected JSON structure: " + responseString;

        } catch (Exception ex) {
            return "Error calling AI model: " + ex.getMessage();
        }
    }

    @Override
    public List<ChatDto> retrieveUserChatsAction(Long userId) {
        return chatRepository.findAllByUserIdOrderByResponseDateAsc(userId)
                .stream()
                .map(c -> new ChatDto(c.getPrompt(), c.getMessage()))
                .toList();
    }

    @Override
    public void deleteChatHistoryAction(Long userId) {
        try {
            List<Chat> messages = chatRepository.findByUserId(userId);
            if (!messages.isEmpty()) chatRepository.deleteAll(messages);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

