package com.halcyon.tinder.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halcyon.tinder.userservice.dto.CreateUserRequest;
import com.halcyon.tinder.userservice.exception.MessageDeserializationException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActionsConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "creatingUser", groupId = "users")
    public void listenCreatingUser(String message) {
        try {
            var createUserRequest = objectMapper.readValue(message, CreateUserRequest.class);
            userService.create(createUserRequest);
        } catch (JsonProcessingException e) {
            throw new MessageDeserializationException("Failed to deserialize SignUpRequest to JSON", e);
        }
    }
}
