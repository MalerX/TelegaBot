package com.malerx.bot.data.repository;

import com.malerx.bot.data.entity.Tenant;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, Long> {
}
