package io.github.jokoframework.security.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.jokoframework.security.entities.TokenEntity;

/**
 * Data access de tokens
 */
public interface ITokenRepository extends JpaRepository<TokenEntity, String> {

    /**
     * Busca el token con id y lo retorna
     *
     * @param jti
     *        identificador de token
     * @return tokenEntity
     *
     */
     TokenEntity getTokenById(String jti);

    /**
     * Retorna todos los tokens activos de un usuario en particular
     *
     * @param userId
     *       identificador de usuario
     * @return lista de tokenEntity
     */
    List<TokenEntity> findByUserId(String userId);

    @Query("SELECT t FROM TokenEntity t WHERE t.userId = :userId order by t.expiration asc")
    List<TokenEntity> findByUser(@Param("userId") String userId);

    @Modifying
    @Query("DELETE from TokenEntity t WHERE t.expiration <= :fromDate")
    int deleteExpiredTokens(@Param("fromDate") Date fromDate);


}
