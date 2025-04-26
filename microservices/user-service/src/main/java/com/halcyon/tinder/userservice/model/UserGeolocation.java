package com.halcyon.tinder.userservice.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "user_geolocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGeolocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point location;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    public UserGeolocation(Point location, User user) {
        this.location = location;
        this.user = user;
    }
}
