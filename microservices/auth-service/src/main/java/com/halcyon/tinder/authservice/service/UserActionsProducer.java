package com.halcyon.tinder.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halcyon.tinder.authservice.dto.user.CreateUserRequest;
import com.halcyon.tinder.authservice.exception.MessageSerializationException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActionsProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void executeCreatingUser(CreateUserRequest createUserRequest) {
        try {
            String message = objectMapper.writeValueAsString(createUserRequest);
            kafkaTemplate.send("creatingUser", message);
        } catch (JsonProcessingException e) {
            throw new MessageSerializationException("Failed to serialize CreateUserRequest to JSON", e);
        }
    }
}
