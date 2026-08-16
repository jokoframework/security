package io.github.jokoframework.security.storage.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.storage.postgres.entity.KeyChainEntity;

public interface IKeychainRepository extends JpaRepository<KeyChainEntity, Integer> {

}
