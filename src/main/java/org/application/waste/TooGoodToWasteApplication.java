package org.application.waste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TooGoodToWasteApplication {

    public static void main(String[] args) {
        SpringApplication.run(TooGoodToWasteApplication.class, args);
    }
}