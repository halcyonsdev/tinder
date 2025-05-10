package com.halcyon.tinder.swipeservice.service;

import com.halcyon.tinder.exceptioncore.UserNotFoundException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.User;
import user.UserServiceGrpc;

@Service
@RequiredArgsConstructor
public class UserServiceGrpcClient {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public UserServiceGrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("user-service", 9090)
                .usePlaintext()
                .build();

        this.userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public User.UserResponse getByPhoneNumber(String phoneNumber) {
        var request = User.PhoneNumberRequest.newBuilder()
                .setPhoneNumber(phoneNumber)
                .build();

        try {
            return userServiceBlockingStub.getByPhoneNumber(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new UserNotFoundException(e.getStatus().getDescription());
            }

            throw new RuntimeException(e);
        }
    }
}
