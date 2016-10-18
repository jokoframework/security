package io.github.jokoframework.security.services.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.jokoframework.security.dto.PrincipalSessionDTO;
import io.github.jokoframework.security.dto.request.PrincipalSessionRequestDTO;
import io.github.jokoframework.security.entities.PrincipalSessionEntity;
import io.github.jokoframework.security.repositories.IPrincipalSessionRepository;
import io.github.jokoframework.security.services.IPrincipalSessionService;

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
        PrincipalSessionEntity entity = principalSessionRepository.findOne(pId);
        BeanUtils.copyProperties(entity, principalDTO);
        return principalDTO;
    }
    
}
