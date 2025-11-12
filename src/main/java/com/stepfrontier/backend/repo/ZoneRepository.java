package com.stepfrontier.backend.repo;

import com.stepfrontier.backend.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByLatitudeAndLongitude(double lat, double lon);

}
