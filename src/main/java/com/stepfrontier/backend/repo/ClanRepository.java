package com.stepfrontier.backend.repo;

import com.stepfrontier.backend.entity.Clan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClanRepository extends JpaRepository<Clan, Long> {
    Optional<Clan> findByName(String name);
}
