package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;
import user.UserServiceGrpc;

@GRpcService
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void existsByPhoneNumber(user.User.PhoneNumberRequest request,
                                    StreamObserver<user.User.ExistsResponse> responseObserver) {
        boolean exists = userRepository.existsByPhoneNumber(request.getPhoneNumber());

        var response = user.User.ExistsResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getByPhoneNumber(user.User.PhoneNumberRequest request, StreamObserver<user.User.UserResponse> responseObserver) {
        Optional<User> userOptional = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if (userOptional.isEmpty()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User with phone number " + request.getPhoneNumber() + " not found")
                            .asRuntimeException());
            return;
        }

        var response = user.User.UserResponse.newBuilder()
                .setPhoneNumber(userOptional.get().getPhoneNumber())
                .setPassword(userOptional.get().getPassword())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
