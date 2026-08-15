package io.github.jokoframework.security.storage.postgres.repository;

import io.github.jokoframework.security.storage.postgres.entity.AuditSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by afeltes on 07/09/16.
 */
public interface IAuditSessionRepository extends JpaRepository<AuditSessionEntity, Long> {

}
