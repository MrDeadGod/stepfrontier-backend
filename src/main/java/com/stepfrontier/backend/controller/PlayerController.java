package com.stepfrontier.backend.controller;

import com.stepfrontier.backend.entity.Player;
import com.stepfrontier.backend.entity.Clan;
import com.stepfrontier.backend.repo.PlayerRepository;
import com.stepfrontier.backend.repo.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final ClanRepository clanRepository;
    private final PasswordEncoder encoder; // ✅ injected here

    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (playerRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // 1️⃣ Create new player (not saved yet)
        Player player = Player.builder()
                .username(username)
                .password(encoder.encode(password))
                .points(20)
                .build();

        // 2️⃣ Create solo clan for this player
        Clan solo = new Clan();
        solo.setName(username + "'s Clan");
        solo.setColor("#2196F3");
        solo.setMaxMembers(1);

        // 3️⃣ Link player <-> clan both ways
        solo.getMembers().add(player);
        player.setClan(solo);

        // 4️⃣ Persist both (thanks to CascadeType.ALL, saving clan saves player too)
        clanRepository.save(solo);

        // 5️⃣ Return the fully initialized player entity (with its clan)
        return ResponseEntity.ok(player);
    }


    @PostMapping("/login")
    public Player login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Player p = playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, p.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return p;
    }

    @GetMapping("/points")
    public Player getPoints(@RequestParam Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    @PostMapping("/spend")
    public Player spend(@RequestParam Long playerId, @RequestParam int cost) {
        Player p = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (p.getPoints() < cost) {
            throw new RuntimeException("Not enough points");
        }

        p.setPoints(p.getPoints() - cost);
        return playerRepository.save(p);
    }

    @PostMapping("/addPoints")
    public Player addPoints(@RequestParam Long playerId, @RequestParam int amount) {
        Player p = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        p.setPoints(p.getPoints() + amount);
        return playerRepository.save(p);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
