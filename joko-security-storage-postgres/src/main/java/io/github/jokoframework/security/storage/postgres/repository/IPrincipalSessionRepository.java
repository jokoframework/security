package io.github.jokoframework.security.storage.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.storage.postgres.entity.PrincipalSessionEntity;

/**
 * 
 * @author bsandoval
 *
 */
public interface IPrincipalSessionRepository extends JpaRepository<PrincipalSessionEntity, Long> {
	PrincipalSessionEntity findByAppIdAndUserId(String appId, String userId);
}
