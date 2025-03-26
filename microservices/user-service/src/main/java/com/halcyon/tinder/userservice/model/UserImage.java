package com.halcyon.tinder.userservice.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "users_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public UserImage(String imageName, User user) {
        this.imageName = imageName;
        this.user = user;
    }
}
