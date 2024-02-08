package com.malerx.bot.services.weather;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.utils.ResourceUtil;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
@Slf4j
public class WeatherStorage {
    private static final String CITY = "city";
    private static final String BODY = "weather";

    private static final String weatherCollection = "weather";
    private static final String SEARCH_AQL = "aql/search_weather.aql";
    private final ArangoCollection weathers;
    private final ArangoDatabase database;
    private final ObjectMapper mapper;

    public WeatherStorage(ArangoDB arangoDB, @Value(value = "${arango.db:}") String db, ObjectMapper mapper) {
        this.database = arangoDB.db(db);
        this.weathers = arangoDB.db(db).collection(weatherCollection);
        this.mapper = mapper;
    }

    public Optional<WeatherData> searchWeather(String city) {
        log.debug("searchWeather() -> search weather for {} in db", city);
        String aql = ResourceUtil.readFile(SEARCH_AQL);
        if (aql == null || aql.isEmpty()) {
            log.error("searchWeather() -> not found search aql");
            return Optional.empty();
        }
        Map<String, Object> bindVars = Map.of(CITY, city);
        try (ArangoCursor<BaseDocument> cursor = database.query(aql, BaseDocument.class, bindVars)) {
            List<BaseDocument> documents = cursor.asListRemaining();
            if (documents.isEmpty())
                return Optional.empty();
            log.debug("searchWeather() -> record was found in the database");
            BaseDocument document = documents.getFirst();
            WeatherData weather = mapper.convertValue(document.getAttribute(BODY), WeatherData.class);
            return Optional.of(weather);
        } catch (Exception e) {
            log.error("error: ", e);
            return Optional.empty();
        }
    }

    public void saveWeather(String city, WeatherData weather) {
        log.debug("saveWeather() -> save weather in db");
        BaseDocument document = createDocument(city, weather);
        weathers.insertDocument(document);
        log.debug("saveWeather() -> weather count docs: {}", weathers.count().getCount());
    }

    private BaseDocument createDocument(String city, WeatherData body) {
        log.debug("createDocument() -> creating document weather");
        BaseDocument document = new BaseDocument(UUID.randomUUID().toString());
        document.addAttribute(CITY, city);
        document.addAttribute(BODY, body);
        document.addAttribute("date", LocalDate.now().toString());
        return document;
    }
}
