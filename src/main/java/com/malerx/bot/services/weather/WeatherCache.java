package com.malerx.bot.services.weather;

import com.arangodb.ArangoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.storage.AbstractCache;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class WeatherCache extends AbstractCache<WeatherData> {

    public WeatherCache(ArangoDatabase database, ObjectMapper mapper) {
        super(database, mapper, WeatherData.class);
    }
}
