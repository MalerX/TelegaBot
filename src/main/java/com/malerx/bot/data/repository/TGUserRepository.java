package com.malerx.bot.data.repository;

import com.malerx.bot.data.entity.TGUser;
import com.malerx.bot.data.enums.Role;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;

@Repository
public interface TGUserRepository extends CrudRepository<TGUser, Long> {
    Collection<TGUser> findByRole(Role role);
}
