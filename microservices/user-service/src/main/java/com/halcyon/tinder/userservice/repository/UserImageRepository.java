package com.halcyon.tinder.userservice.repository;

import com.halcyon.tinder.userservice.model.UserImage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, UUID> {}
