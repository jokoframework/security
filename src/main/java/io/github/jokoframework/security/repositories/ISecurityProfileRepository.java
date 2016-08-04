package io.github.jokoframework.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.entities.SecurityProfile;

import java.util.List;

public interface ISecurityProfileRepository extends JpaRepository<SecurityProfile, Long> {

    List<SecurityProfile> getProfileByKeyOrderByIdDesc(String key);

}
