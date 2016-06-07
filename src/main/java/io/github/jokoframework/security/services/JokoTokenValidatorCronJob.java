package io.github.jokoframework.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.security.controller.SecurityConstants;

@Component
public class JokoTokenValidatorCronJob {

    private static final Logger logger = LoggerFactory.getLogger(JokoTokenValidatorCronJob.class);

    @Autowired
    private ITokenService tokenService;

    // Cada 5 minutos controla los tokens expirados y los elimina
    //FIXME poner esto en un parametro. 
    @Scheduled(fixedRate = SecurityConstants.TOKEN_REMOVAL_INTERVAL)
    public void deleteExpiredTokens() {
        int deleteExpiredTokens = tokenService.deleteExpiredTokens();

        if (deleteExpiredTokens > 0) {
            logger.info("Removed {} exipired tokens", JokoUtils.formatLogString(deleteExpiredTokens));
        }

    }
}
