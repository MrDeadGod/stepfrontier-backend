package com.stepfrontier.backend.controller;

import com.stepfrontier.backend.entity.Clan;
import com.stepfrontier.backend.entity.Player;
import com.stepfrontier.backend.repo.ClanRepository;
import com.stepfrontier.backend.repo.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequestMapping("/api/v1/clans")
@RequiredArgsConstructor
public class ClanController {

    private final ClanRepository clanRepository;
    private final PlayerRepository playerRepository;

    @GetMapping
    public List<Clan> getAll() {
        return clanRepository.findAll();
    }

    @PostMapping("/join")
    public ResponseEntity<Player> joinClan(@RequestParam Long playerId, @RequestParam Long clanId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clan not found"));

        // ✅ Optional: prevent overfilling
        if (clan.getMembers().size() >= clan.getMaxMembers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clan is full");
        }

        // ✅ Remove from old clan if any
        if (player.getClan() != null) {
            Clan oldClan = player.getClan();
            oldClan.getMembers().remove(player);
            clanRepository.save(oldClan);
        }

        // ✅ Join new clan
        player.setClan(clan);
        playerRepository.save(player);

        // Ensure both sides of relationship are consistent
        clan.getMembers().add(player);
        clanRepository.save(clan);

        return ResponseEntity.ok(player);
    }

    @GetMapping("/{clanId}/members")
    public ResponseEntity<List<Player>> getClanMembers(@PathVariable Long clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clan not found"));
        List<Player> members = playerRepository.findByClanId(clan.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/create")
    public Clan createClan(@RequestParam String name, @RequestParam Long playerId) {
        Player leader = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        // leave current clan
        if (leader.getClan() != null) {
            leader.getClan().getMembers().remove(leader);
        }

        Clan clan = new Clan();
        clan.setName(name);
        clan.setMaxMembers(10);
        clan.getMembers().add(leader);

        clanRepository.save(clan);
        leader.setClan(clan);
        playerRepository.save(leader);

        return clan;
    }

}
