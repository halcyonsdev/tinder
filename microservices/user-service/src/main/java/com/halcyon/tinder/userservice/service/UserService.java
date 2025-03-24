package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.dto.user.UserProfileDto;
import com.halcyon.tinder.userservice.dto.user.UserPutRequest;
import com.halcyon.tinder.userservice.exception.UserNotFoundException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.UserImage;
import com.halcyon.tinder.userservice.repository.UserImageRepository;
import com.halcyon.tinder.userservice.repository.UserRepository;
import com.halcyon.tinder.userservice.security.JwtProvider;
import com.halcyon.tinder.userservice.service.support.ImageData;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final ImageStorageService imageStorageService;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User with phone number " + phoneNumber + " not found"));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public UserProfileDto getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return userMapper.toProfile(currentUser);
    }

    public User getCurrentUser() {
        String phoneNumber = jwtProvider.getJwtAuthentication().getPhoneNumber();
        return findByPhoneNumber(phoneNumber);
    }

    public UserProfileDto getUserProfileById(UUID userId) {
        return userMapper.toProfile(findById(userId));
    }

    private User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    public UserProfileDto update(UserPutRequest userPutRequest) {
        User user = getCurrentUser();

        userMapper.updateUserFromPutRequest(userPutRequest, user);

        if (user.getPreferences() != null) {
            user.getPreferences().setUser(user);
        }

        user = save(user);

        return userMapper.toProfile(user);
    }

    public UserProfileDto uploadAvatar(MultipartFile image) {
        User user = getCurrentUser();

        if (user.getAvatar() != null) {
            imageStorageService.deleteImage(user.getAvatar());
        }

        String imageName = imageStorageService.uploadImage(image);
        user.setAvatar(imageName);

        return userMapper.toProfile(save(user));
    }

    public ImageData downloadImage(String imageName) {
        return imageStorageService.downloadImage(imageName);
    }

    public UserProfileDto uploadGalleryImages(List<MultipartFile> images) {
        User user = getCurrentUser();

        for (var image : images) {
            String imageName = imageStorageService.uploadImage(image);

            var userImage = new UserImage(imageName, user);
            userImageRepository.save(userImage);
        }

        return userMapper.toProfile(user);
    }

    public List<String> getGallery(UUID userId) {
        User user = findById(userId);

        return user.getGallery()
                .stream()
                .map(UserImage::getImageName)
                .toList();
    }
}
