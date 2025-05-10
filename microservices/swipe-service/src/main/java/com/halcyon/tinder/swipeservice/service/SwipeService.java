package com.halcyon.tinder.swipeservice.service;

import com.halcyon.tinder.jwtcore.JwtProvider;
import com.halcyon.tinder.swipeservice.exception.InvalidSwipeException;
import com.halcyon.tinder.swipeservice.exception.SwipeAlreadyExistsException;
import com.halcyon.tinder.swipeservice.model.Swipe;
import com.halcyon.tinder.swipeservice.repository.SwipeRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.User;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final JwtProvider jwtProvider;
    private final UserServiceGrpcClient userServiceGrpcClient;

    public void swipe(UUID toSwipeUserId, Boolean decision) {
        String userPhoneNumber = jwtProvider.getJwtAuthentication().getPhoneNumber();
        User.UserResponse swiper = userServiceGrpcClient.getByPhoneNumber(userPhoneNumber);
        UUID swiperId = UUID.fromString(swiper.getId());

        validateSwipeRequest(swiperId, toSwipeUserId);

        if (swipeRepository.existsByFirstSwiperIdAndSecondSwiperId(toSwipeUserId, swiperId)) {
            swipeSecond(toSwipeUserId, swiperId, decision);
        } else {
            swipeFirst(swiperId, toSwipeUserId, decision);
        }
    }

    private void validateSwipeRequest(UUID swiperId, UUID toSwipeUserId) {
        if (swiperId.equals(toSwipeUserId)) {
            throw new InvalidSwipeException("You cannot swipe yourself.");
        }

        if (swipeRepository.existsByFirstSwiperIdAndSecondSwiperId(swiperId, toSwipeUserId) ||
                swipeRepository.existsByFirstSwiperIdAndSecondSwiperIdAndSecondSwiperDecisionIsNotNull(toSwipeUserId, swiperId)) {
            throw new SwipeAlreadyExistsException("You already swiped this user");
        }
    }

    private void swipeSecond(UUID firstSwiperId, UUID secondSwiperId, Boolean decision) {
        Swipe swipe = swipeRepository.updateSecondSwiperDecision(decision, firstSwiperId, secondSwiperId);

        if (swipe.getFirstSwiperDecision().equals(swipe.getSecondSwiperDecision())) {
            // TODO: send notification to swipers about their match
        }
    }

    private void swipeFirst(UUID firstSwiperId, UUID secondSwiperId, Boolean decision) {
        var swipe = Swipe.builder()
                .firstSwiperId(firstSwiperId)
                .firstSwiperDecision(decision)
                .secondSwiperId(secondSwiperId)
                .build();

        swipeRepository.save(swipe);
    }
}
