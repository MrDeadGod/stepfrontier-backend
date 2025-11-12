package com.stepfrontier.backend.service;

import com.stepfrontier.backend.entity.Player;
import com.stepfrontier.backend.repo.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final PlayerRepository repo;

    public PlayerService(PlayerRepository repo) {
        this.repo = repo;
    }

    public Player getOrCreate(String username) {
        return repo.findByUsername(username)
                .orElseGet(() -> repo.save(Player.builder()
                        .username(username)
                        .points(500) // starting points
                        .build()));
    }

    public Player spendPoints(String username, int cost) {
        Player p = getOrCreate(username);
        if (p.getPoints() < cost) throw new RuntimeException("Not enough points");
        p.setPoints(p.getPoints() - cost);
        return repo.save(p);
    }

    public Player addPoints(String username, int amount) {
        Player p = getOrCreate(username);
        p.setPoints(p.getPoints() + amount);
        return repo.save(p);
    }
}
