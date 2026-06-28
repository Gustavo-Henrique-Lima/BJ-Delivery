package com.bj_food.ms_usuario.kafka.consumer;

import com.bj_food.ms_usuario.dto.request.CreateUserRequest;
import com.bj_food.ms_usuario.repository.UserRepository;
import com.bj_food.ms_usuario.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final UserService userService;
    private final UserRepository userRepository;

    public UserEventConsumer(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "usuario.criado", groupId = "usuario-group")
    public void onUserCreated(Map<String, String> event) {
        String keycloakId = event.get("keycloakId");
        String email = event.get("email");
        String name = event.get("name");

        log.info("Received usuario.criado event for keycloakId={}", keycloakId);

        if (userRepository.existsByEmail(email)) {
            log.warn("User already exists for email={}, skipping", email);
            return;
        }

        CreateUserRequest request = new CreateUserRequest(name, email, null, keycloakId);
        userService.create(request);

        log.info("User created from Kafka event keycloakId={}", keycloakId);
    }
}