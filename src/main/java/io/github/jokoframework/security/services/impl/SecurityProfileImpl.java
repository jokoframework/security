package io.github.jokoframework.security.services.impl;

import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.security.entities.SecurityProfile;
import io.github.jokoframework.security.errors.JokoUnauthorizedException;
import io.github.jokoframework.security.repositories.ISecurityProfileRepository;
import io.github.jokoframework.security.services.ISecurityProfileService;
import io.github.jokoframework.security.util.TXUUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SecurityProfileImpl implements ISecurityProfileService {

    private static final int UUID_LENGTH = 12;
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityProfileImpl.class);

    @Autowired
    private ISecurityProfileRepository appRepository;

    private TXUUIDGenerator appGenerator = new TXUUIDGenerator(UUID_LENGTH);

    private ConcurrentMap<String, SecurityProfile> appCache = new ConcurrentHashMap<>();

    @Override
    public SecurityProfile getProfileByKey(String key) {
        if (key == null) {
            return null;
        }
        SecurityProfile profile;
        profile = appCache.get(key);
        if (profile == null) {
            LOGGER.debug("Security Profile {} is not on cache. Loading it from the DB", JokoUtils.formatLogString(key));
            List<SecurityProfile> profileList = appRepository.getProfileByKeyOrderByIdDesc(key);
            if (profileList != null && !profileList.isEmpty()) {
                profile = profileList.get(0);
                SecurityProfile ent = appCache.putIfAbsent(key, profile);
                if (ent == null) {
                    LOGGER.debug("Saving {} on cache", JokoUtils.formatLogString(profile.getKey()));
                }
            } else {
                LOGGER.error("The security profile {} was neither on cache nor on DB", JokoUtils.formatLogString(key));
            }
        }

        return profile;
    }

    @Override
    public SecurityProfile save(SecurityProfile entity) {
        if (entity.getKey() == null) {
            String uuid = appGenerator.generate();
            entity.setKey(uuid);
        }
        SecurityProfile saved = appRepository.save(entity);
        return saved;
    }

    @Override
    public SecurityProfile getOrSaveProfile(String key, SecurityProfile app) {
        SecurityProfile appStored = getProfileByKey(key);
        if (appStored == null) {
            // se asegura que el id que se usa es el que no se acaba de
            // encontrar
            app.setKey(key);
            appStored = save(app);
        }
        return appStored;
    }

    @Override
    public SecurityProfile getApplicationByKeySafety(String key, boolean throwIFDoesntExists) {
        SecurityProfile app = getProfileByKey(key);
        if (app == null && throwIFDoesntExists) {
            // Esto es directamente un estado indesesado
            // Si alguien ya esta usando el metodo safety es porque asume que la
            // aplicacion deberia de existir
            // El metodo de login controla que la aplicaacion exista entonces en
            // este punto ya deberia de existir la aplicacion
            LOGGER.warn("Can't login without a registered aplication");
            throw new JokoUnauthorizedException("Not a valid application");
        }
        return app;
    }

}
