package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.exception.ImageNotFoundException;
import com.halcyon.tinder.userservice.exception.ImageStorageException;
import com.halcyon.tinder.userservice.service.support.ImageData;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    public String uploadImage(MultipartFile image) {
        try (InputStream inputStream = image.getInputStream()) {
            String imageName = UUID.randomUUID() + "." + getExtension(Objects.requireNonNull(image.getOriginalFilename()));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(imageName)
                            .stream(inputStream, image.getSize(), -1)
                            .contentType(image.getContentType())
                            .build());

            return imageName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension(String imageName) {
        if (!imageName.contains(".") || ALLOWED_EXTENSIONS.contains(imageName)) {
            throw new ImageStorageException("Image must have the extension jpg/png/jpeg");
        }

        return imageName.substring(imageName.lastIndexOf(".") + 1);
    }

    public ImageData downloadImage(String imageName) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(imageName)
                            .build());

            String contentType = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(imageName)
                            .build())
                    .contentType();

            return new ImageData(imageName, MediaType.parseMediaType(contentType), new InputStreamResource(inputStream));
        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                throw new ImageNotFoundException("Image with name " + imageName + " not found");
            }

            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteImage(String imageName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(imageName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
