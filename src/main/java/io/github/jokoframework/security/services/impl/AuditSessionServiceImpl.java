package io.github.jokoframework.security.services.impl;

import io.github.jokoframework.security.dto.AuditSessionDTO;
import io.github.jokoframework.security.dto.request.AuditSessionRequestDTO;
import io.github.jokoframework.security.dto.response.AuditSessionResponseDTO;
import io.github.jokoframework.security.entities.AuditSessionEntity;
import io.github.jokoframework.security.repositories.IAuditSessionRepository;
import io.github.jokoframework.security.services.IAuditSessionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by afeltes on 07/09/16.
 */
@Service
public class AuditSessionServiceImpl implements IAuditSessionService {
    @Autowired
    private IAuditSessionRepository auditSessionRepository;

    @Override
    public List<AuditSessionResponseDTO> findAllOrderdByUserDate(Integer startPage, Integer rowsPerPage) {
        List<AuditSessionResponseDTO> sessionsPage = new ArrayList<>();
        Pageable pageable = new PageRequest(startPage, rowsPerPage, new Sort(Sort.Direction.DESC, AuditSessionEntity.USER_DATE));
        Page<AuditSessionEntity> sessions = auditSessionRepository.findAll(pageable);
        for (AuditSessionEntity entity : sessions) {
            AuditSessionResponseDTO dto = new AuditSessionResponseDTO();
            BeanUtils.copyProperties(entity, dto);
            sessionsPage.add(dto);
        }
        return sessionsPage;
    }

    @Override
    public AuditSessionDTO save(AuditSessionRequestDTO pAuditSessionDTO) {
        AuditSessionEntity pAuditSessionEntity = new AuditSessionEntity();
        BeanUtils.copyProperties(pAuditSessionDTO, pAuditSessionEntity);
        AuditSessionEntity entity = auditSessionRepository.save(pAuditSessionEntity);
        AuditSessionDTO dto = new AuditSessionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public AuditSessionDTO findById(Long pId) {
        AuditSessionDTO auditDTO = new AuditSessionDTO();
        AuditSessionEntity entity = auditSessionRepository.findOne(pId);
        BeanUtils.copyProperties(entity, auditDTO);
        return auditDTO;
    }
}
