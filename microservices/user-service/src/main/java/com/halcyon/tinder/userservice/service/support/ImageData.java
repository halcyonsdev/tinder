package com.halcyon.tinder.userservice.service.support;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record ImageData(String imageName, MediaType mediaType, Resource resource) {}
