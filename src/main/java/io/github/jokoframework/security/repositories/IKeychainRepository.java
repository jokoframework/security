package io.github.jokoframework.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.entities.KeyChainEntity;

public interface IKeychainRepository extends JpaRepository<KeyChainEntity, Integer> {

}
