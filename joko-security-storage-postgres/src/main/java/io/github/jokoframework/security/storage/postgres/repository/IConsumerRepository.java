package io.github.jokoframework.security.storage.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.storage.postgres.entity.ConsumerApiEntity;

public interface IConsumerRepository extends JpaRepository<ConsumerApiEntity, Long> {

    ConsumerApiEntity getUserApiAccessByConsumerId(String consumerId);
    ConsumerApiEntity getUserApiAccessByName(String name);

}
