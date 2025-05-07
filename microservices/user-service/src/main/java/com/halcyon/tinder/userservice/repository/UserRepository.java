package com.halcyon.tinder.userservice.repository;

import com.halcyon.tinder.userservice.dto.UserProfileDto;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.model.support.Gender;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
                SELECT new com.halcyon.tinder.userservice.dto.UserProfileDto(
                       u.id, u.createdAt, u.phoneNumber, u.firstName, u.lastName, u.age,
                       u.gender, u.bio, u.avatar, u.interests,
                       new com.halcyon.tinder.userservice.dto.UserPreferencesDto(p.gender, p.minAge, p.maxAge, p.distanceInMeters),
                       new com.halcyon.tinder.userservice.dto.UserGeolocationDto(g.location)
                )
                FROM User u
                LEFT JOIN u.geolocation g
                LEFT JOIN u.preferences p
                WHERE u.id != :currentUserId
                AND (:minAge IS NULL OR u.age >= :minAge)
                AND (:maxAge IS NULL OR u.age <= :maxAge)
                AND (:gender IS NULL OR u.gender = :gender)
                AND (
                    (:latitude IS NULL OR :longitude IS NULL)
                    OR (
                        g.location IS NOT NULL
                        AND (
                            ST_Distance(
                                g.location,
                                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                            ) <= :distanceInMeters
                        )
                    )
                )
            """)
    List<UserProfileDto> findDeck(
                                  @Param("currentUserId") UUID currentUserId,
                                  @Param("minAge") Integer minAge,
                                  @Param("maxAge") Integer maxAge,
                                  @Param("gender") Gender gender,
                                  @Param("longitude") Double longitude,
                                  @Param("latitude") Double latitude,
                                  @Param("distanceInMeters") Double distanceInMeters);
}
