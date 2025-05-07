package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.rediscache.CacheManager;
import com.halcyon.tinder.userservice.dto.UserProfileDto;
import com.halcyon.tinder.userservice.exception.AccessDeniedException;
import com.halcyon.tinder.userservice.exception.ImageNotFoundException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.UserImage;
import com.halcyon.tinder.userservice.repository.UserImageRepository;
import com.halcyon.tinder.userservice.service.support.ImageData;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserImageService {

    private final UserService userService;
    private final ImageStorageService imageStorageService;
    private final CacheManager cacheManager;
    private final UserMapper userMapper;
    private final UserImageRepository userImageRepository;

    private static final String USER_CACHE_PREFIX = "user-profile:";

    public UserProfileDto uploadAvatar(MultipartFile image) {
        User user = userService.getCurrentUser();

        if (user.getAvatar() != null) {
            imageStorageService.deleteImage(user.getAvatar());
        }

        String imageName = imageStorageService.uploadImage(image);
        user.setAvatar(imageName);

        cacheManager.delete(USER_CACHE_PREFIX + user);
        return userMapper.toProfile(userService.save(user));
    }

    public ImageData downloadImage(String imageName) {
        return imageStorageService.downloadImage(imageName);
    }

    public UserProfileDto uploadGalleryImages(List<MultipartFile> images) {
        User user = userService.getCurrentUser();

        for (var image : images) {
            String imageName = imageStorageService.uploadImage(image);

            var userImage = new UserImage(imageName, user);
            userImageRepository.save(userImage);
        }

        return userMapper.toProfile(user);
    }

    public List<String> getGallery(UUID userId) {
        User user = userService.findById(userId);

        return user.getGallery()
                .stream()
                .map(UserImage::getImageName)
                .toList();
    }

    public UserProfileDto deleteAvatar() {
        User user = userService.getCurrentUser();

        if (user.getAvatar() != null) {
            imageStorageService.deleteImage(user.getAvatar());
        }

        user.setAvatar(null);

        cacheManager.delete(USER_CACHE_PREFIX + user);
        return userMapper.toProfile(userService.save(user));
    }

    public void deleteGalleryImage(String imageName) {
        User user = userService.getCurrentUser();
        UserImage userImage = userImageRepository.findByImageName(imageName)
                .orElseThrow(() -> new ImageNotFoundException("Image with name " + imageName + " not found in gallery"));

        if (!userImage.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("This image belongs to another user");
        }

        imageStorageService.deleteImage(imageName);
        userImageRepository.delete(userImage);
    }
}
