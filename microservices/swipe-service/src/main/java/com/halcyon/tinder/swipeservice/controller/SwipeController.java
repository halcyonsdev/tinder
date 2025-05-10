package com.halcyon.tinder.swipeservice.controller;

import com.halcyon.tinder.swipeservice.service.SwipeService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void swipe(@RequestParam("userId") UUID toSwipeUserId, @RequestParam("decision") Boolean decision) {
        swipeService.swipe(toSwipeUserId, decision);
    }
}
