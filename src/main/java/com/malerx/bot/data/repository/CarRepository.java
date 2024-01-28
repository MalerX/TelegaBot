package com.malerx.bot.data.repository;

import com.malerx.bot.data.entity.Car;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface CarRepository extends CrudRepository<Car, Long> {
}
