package io.github.jokoframework.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.entities.PrincipalSessionEntity;

/**
 * 
 * @author bsandoval
 *
 */
public interface IPrincipalSessionRepository extends JpaRepository<PrincipalSessionEntity, Long> {
	PrincipalSessionEntity findByAppIdAndUserId(String appId, String userId);
}
