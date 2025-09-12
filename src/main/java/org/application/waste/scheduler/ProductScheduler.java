package org.application.waste.scheduler;

import org.application.waste.entity.User;
import org.application.waste.repository.UserRepository;
import org.application.waste.service.ProductLinkService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductScheduler {
    private final UserRepository userRepository;
    private final ProductLinkService productFileService;

    public ProductScheduler(UserRepository userRepository, ProductLinkService productLinkService) {
        this.userRepository = userRepository;
        this.productFileService = productLinkService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void updateProductsFromAllLinks() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getProductLink() != null) {
                String link = user.getProductLink().getOriginalLink();

                if (link != null && !link.isEmpty()) {
                    try {
                        productFileService.saveProductsFromLink(link, user.getId());
                    } catch (Exception e) {
                        throw new RuntimeException("Error updating products for current user");
                    }
                }
            }
        }
    }
}