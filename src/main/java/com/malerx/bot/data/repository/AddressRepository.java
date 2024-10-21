package com.malerx.bot.data.repository;

import com.malerx.bot.data.entity.Address;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {
}
