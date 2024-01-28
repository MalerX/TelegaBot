package com.malerx.bot.data.repository;

import com.malerx.bot.data.entity.GpgRecord;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface PgpRepository extends CrudRepository<GpgRecord, Long> {
}
