package com.stepfrontier.backend.controller;

import com.stepfrontier.backend.entity.Zone;
import com.stepfrontier.backend.entity.Player;
import com.stepfrontier.backend.entity.Clan;
import com.stepfrontier.backend.repo.ZoneRepository;
import com.stepfrontier.backend.repo.PlayerRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/zones")
public class ZoneController {

    private final ZoneRepository zoneRepository;
    private final PlayerRepository playerRepository;

    public ZoneController(ZoneRepository zoneRepository,
                          PlayerRepository playerRepository) {
        this.zoneRepository = zoneRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    @DeleteMapping("/reset")
    public void resetZones() {
        zoneRepository.deleteAll();
    }


    @PostMapping("/capture")
    public Zone captureZone(@RequestBody Map<String, Object> body) {

        double latitude = ((Number) body.get("latitude")).doubleValue();
        double longitude = ((Number) body.get("longitude")).doubleValue();
        Number idNum = (Number) body.get("playerId");
        Long playerId = idNum.longValue();



        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Clan clan = player.getClan();
        if (clan == null) {
            throw new RuntimeException("Player has no clan — cannot capture zone");
        }

        return zoneRepository.findByLatitudeAndLongitude(latitude, longitude)
                .map(existing -> {
                    existing.setOwner(clan);
                    existing.setResistance(existing.getResistance() + 1);
                    existing.setLastUpdated(Instant.now());
                    return zoneRepository.save(existing);
                })
                .orElseGet(() -> zoneRepository.save(
                        Zone.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .owner(clan)
                                .resistance(1)
                                .lastUpdated(Instant.now())
                                .build()
                ));
    }
}
