package com.halcyon.tinder.swipeservice.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "swipes")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_swiper_id")
    private UUID firstSwiperId;

    @Column(name = "second_swiper_id")
    private UUID secondSwiperId;

    @Column(name = "first_swiper_decision")
    private Boolean firstSwiperDecision;

    @Column(name = "second_swiper_decision")
    private Boolean secondSwiperDecision;
}
