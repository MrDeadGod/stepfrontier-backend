package com.stepfrontier.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clan_id")
    @JsonIgnoreProperties({"members", "ownedZones"}) // stop circular JSON
    private Clan owner;

    private int resistance;

    private Instant lastUpdated;

    // ✅ Custom JSON view for Flutter
    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    public java.util.Map<String, Object> getOwnerRef() {
        if (owner == null) return null;

        java.util.Map<String, Object> ref = new java.util.HashMap<>();
        ref.put("name", owner.getName());
        // Optional: include color if you want to show clan colors in Flutter
        ref.put("color", owner.getColor());
        return ref;
    }

}
