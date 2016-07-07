package io.github.jokoframework.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jokoframework.security.entities.ConsumerApiEntity;

public interface IConsumerRepository extends JpaRepository<ConsumerApiEntity, Long> {

    ConsumerApiEntity getUserApiAccessByConsumerId(String consumerId);
    ConsumerApiEntity getUserApiAccessByName(String name);

}
