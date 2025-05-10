package com.halcyon.tinder.swipeservice.repository;

import com.halcyon.tinder.swipeservice.model.Swipe;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    boolean existsByFirstSwiperIdAndSecondSwiperId(UUID firstSwiperId, UUID secondSwiperId);

    boolean existsByFirstSwiperIdAndSecondSwiperIdAndSecondSwiperDecisionIsNotNull(UUID firstSwiperId, UUID secondSwiperId);

    @Query(value = """
                UPDATE swipes
                SET second_swiper_decision = :decision
                WHERE first_swiper_id = :firstSwiperId
                AND second_swiper_id = :secondSwiperId
                RETURNING *
            """, nativeQuery = true)
    Swipe updateSecondSwiperDecision(Boolean decision, UUID firstSwiperId, UUID secondSwiperId);
}
