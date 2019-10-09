package io.github.jokoframework.security.services.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.jokoframework.security.dto.PrincipalSessionDTO;
import io.github.jokoframework.security.dto.request.PrincipalSessionRequestDTO;
import io.github.jokoframework.security.entities.PrincipalSessionEntity;
import io.github.jokoframework.security.repositories.IPrincipalSessionRepository;
import io.github.jokoframework.security.services.IPrincipalSessionService;

import java.util.Optional;

/**
 * 
 * @author bsandoval
 *
 */
@Service
public class PrincipalSessionServiceImpl implements IPrincipalSessionService{
    @Autowired
	private IPrincipalSessionRepository principalSessionRepository;
    
    @Override
	public PrincipalSessionDTO findByAppIdAndUserId(String appId, String userId) {
        PrincipalSessionEntity entity = principalSessionRepository.findByAppIdAndUserId(appId, userId);
        PrincipalSessionDTO session = new PrincipalSessionDTO();
        BeanUtils.copyProperties(entity, session);
        return session;
	}

    @Override
    public PrincipalSessionDTO save(PrincipalSessionRequestDTO pPrincipalSessionTO) {
        PrincipalSessionEntity pPrincipalSessionEntity = new PrincipalSessionEntity();
        BeanUtils.copyProperties(pPrincipalSessionTO, pPrincipalSessionEntity);
        PrincipalSessionEntity entity = principalSessionRepository.save(pPrincipalSessionEntity);
        PrincipalSessionDTO dto = new PrincipalSessionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public PrincipalSessionDTO findById(Long pId) {
        PrincipalSessionDTO principalDTO = new PrincipalSessionDTO();
        Optional<PrincipalSessionEntity> entity = principalSessionRepository.findById(pId);
        entity.ifPresent(e -> BeanUtils.copyProperties(e, principalDTO));
        return principalDTO;
    }
    
}
