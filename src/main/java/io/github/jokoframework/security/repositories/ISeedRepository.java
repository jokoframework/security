package io.github.jokoframework.security.repositories;

import io.github.jokoframework.security.entities.SeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ISeedRepository extends JpaRepository<SeedEntity, Long> {

    Optional<SeedEntity> findOneByUserId(String user_Id);
}
