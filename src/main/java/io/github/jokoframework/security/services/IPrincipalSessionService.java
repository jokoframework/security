package io.github.jokoframework.security.services;

import io.github.jokoframework.security.dto.PrincipalSessionDTO;
import io.github.jokoframework.security.dto.request.PrincipalSessionRequestDTO;

/**
 * 
 * @author bsandoval
 *
 */
public interface IPrincipalSessionService {
	PrincipalSessionDTO findByAppIdAndUserId(String appId, String userId);
	PrincipalSessionDTO save(PrincipalSessionRequestDTO pPrincipalSession);
	PrincipalSessionDTO findById(Long id);
}
