package com.stepfrontier.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "clans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"members", "ownedZones"}) // prevent recursion on both lists
public class Clan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color = "#2196F3"; // default blue marker color

    private String emblemUrl; // optional icon for later use

    @Column(nullable = false)
    private int maxMembers = 10;

    // players in this clan
    @OneToMany(mappedBy = "clan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("clan") // stops back-reference
    private List<Player> members = new ArrayList<>();

    // zones owned by this clan
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("owner") // stops back-reference
    private List<Zone> ownedZones = new ArrayList<>();
}
