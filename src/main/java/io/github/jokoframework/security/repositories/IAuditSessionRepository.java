package io.github.jokoframework.security.repositories;

import io.github.jokoframework.security.entities.AuditSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by afeltes on 07/09/16.
 */
public interface IAuditSessionRepository extends JpaRepository<AuditSessionEntity, Long> {

}
