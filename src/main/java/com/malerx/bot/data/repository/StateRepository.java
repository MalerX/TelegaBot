package com.malerx.bot.data.repository;

import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.enums.Stage;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;

@Repository
public interface StateRepository extends CrudRepository<PersistState, Long> {
    @Query("FROM PersistState s WHERE s.chatId = :id AND s.stage = :stage ORDER BY s.process")
    Collection<PersistState> findActiveProcess(Long id, Stage stage);
}
