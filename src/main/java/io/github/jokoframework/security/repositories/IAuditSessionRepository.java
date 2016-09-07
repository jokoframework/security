package io.github.jokoframework.security.repositories;

import io.github.jokoframework.security.entities.AuditSessionEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by afeltes on 07/09/16.
 */
public interface IAuditSessionRepository extends PagingAndSortingRepository<AuditSessionEntity, Long> {

}
