package io.github.jokoframework.security.services;

import io.github.jokoframework.security.dto.AuditSessionDTO;
import io.github.jokoframework.security.dto.request.AuditSessionRequestDTO;
import io.github.jokoframework.security.dto.response.AuditSessionResponseDTO;
import io.github.jokoframework.security.entities.AuditSessionEntity;

import java.util.List;

/**
 * Created by afeltes on 07/09/16.
 */
public interface IAuditSessionService {
    List<AuditSessionResponseDTO> findAllOrderdByUserDate(Integer startPage, Integer rowsPerPage);
    AuditSessionDTO save(AuditSessionRequestDTO pAuditSession);
    AuditSessionDTO findById(Long id);
}
