package io.github.jokoframework.security.services;

import java.util.List;

import io.github.jokoframework.security.dto.AuditSessionDTO;
import io.github.jokoframework.security.dto.request.AuditSessionRequestDTO;
import io.github.jokoframework.security.dto.response.AuditSessionResponseDTO;

/**
 * Created by afeltes on 07/09/16.
 */
public interface IAuditSessionService {
    List<AuditSessionResponseDTO> findAllOrderdByUserDate(Integer startPage, Integer rowsPerPage);
    AuditSessionDTO save(AuditSessionRequestDTO pAuditSession);
    AuditSessionDTO findById(Long id);
}
