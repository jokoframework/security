package io.github.jokoframework.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.entities.SecurityProfile;

public interface ISecurityProfileRepository extends JpaRepository<SecurityProfile, Long> {

    public SecurityProfile getProfileByKey(String key);

}
