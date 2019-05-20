package io.github.jokoframework.security.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.jokoframework.security.dto.AuditSessionDTO;
import io.github.jokoframework.security.dto.PrincipalSessionDTO;
import io.github.jokoframework.security.dto.request.AuditSessionRequestDTO;
import io.github.jokoframework.security.dto.request.PrincipalSessionRequestDTO;
import io.github.jokoframework.security.dto.response.AuditSessionResponseDTO;
import io.github.jokoframework.security.entities.AuditSessionEntity;
import io.github.jokoframework.security.entities.PrincipalSessionEntity;
import io.github.jokoframework.security.repositories.IAuditSessionRepository;
import io.github.jokoframework.security.repositories.IPrincipalSessionRepository;
import io.github.jokoframework.security.services.IAuditSessionService;

/**
 * Created by afeltes on 07/09/16.
 */
@Service
public class AuditSessionServiceImpl implements IAuditSessionService {
	
    @Autowired
    private IAuditSessionRepository auditSessionRepository;
    
    @Autowired
    private IPrincipalSessionRepository principalSessionRepository; 

    @Override
    public List<AuditSessionResponseDTO> findAllOrderdByUserDate(Integer startPage, Integer rowsPerPage) {
        List<AuditSessionResponseDTO> sessionsPage = new ArrayList<>();
        Pageable pageable = new PageRequest(startPage, rowsPerPage, new Sort(Sort.Direction.DESC, AuditSessionEntity.USER_DATE));
        Page<AuditSessionEntity> sessions = auditSessionRepository.findAll(pageable);
        for (AuditSessionEntity entity : sessions) {
            AuditSessionResponseDTO dto = new AuditSessionResponseDTO();
            BeanUtils.copyProperties(entity, dto);
            PrincipalSessionDTO principal = new PrincipalSessionDTO();
            BeanUtils.copyProperties(entity.getPrincipal(), principal);
            dto.setPrincipal(principal);
            sessionsPage.add(dto);
        }
        return sessionsPage;
    }

    @Override
    public AuditSessionDTO save(AuditSessionRequestDTO pAuditSessionDTO) {
    	AuditSessionEntity pAuditSessionEntity = from(pAuditSessionDTO);
    	if(pAuditSessionDTO.getPrincipal() != null){
    		PrincipalSessionEntity principal = principalSessionRepository.findByAppIdAndUserId(pAuditSessionDTO.getPrincipal().getAppId(), pAuditSessionDTO.getPrincipal().getUserId());
    		if(principal != null) pAuditSessionEntity.setPrincipal(principal);
    	}
    	pAuditSessionEntity.setCreationDate(new Date(System.currentTimeMillis()));
        AuditSessionEntity entity = auditSessionRepository.save(pAuditSessionEntity);
        AuditSessionDTO dto = new AuditSessionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public AuditSessionDTO findById(Long pId) {
        AuditSessionDTO auditDTO = new AuditSessionDTO();
        Optional<AuditSessionEntity> entity = auditSessionRepository.findById(pId);
        entity.ifPresent(e ->
                BeanUtils.copyProperties(e, auditDTO));
        return auditDTO;
    }
    
    private AuditSessionEntity from(AuditSessionRequestDTO auditSession) {
    	AuditSessionEntity entity = new AuditSessionEntity();
    	entity.setRemoteIp(auditSession.getRemoteIp());
    	entity.setUserAgent(auditSession.getUserAgent());
    	entity.setUserDate(auditSession.getUserDate());
    	entity.setPrincipal(from(auditSession.getPrincipal()));
    	return entity;
    }

    private PrincipalSessionEntity from(PrincipalSessionRequestDTO principalSession) {
    	PrincipalSessionEntity entity = new PrincipalSessionEntity();
    	entity.setAppId(principalSession.getAppId());
    	entity.setAppDescription(principalSession.getAppDescription());
    	entity.setUserId(principalSession.getUserId());
    	entity.setUserDescription(principalSession.getUserDescription());
    	return entity;
    }
}