package com.malerx.bot.factory;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.serde.jackson.JacksonSerde;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.malerx.bot.handlers.commands.impl.CustomBodyHandler;
import com.malerx.bot.services.exchange.Exchange;
import com.malerx.bot.services.weather.GeoData;
import com.malerx.bot.services.weather.WeatherData;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;

@Factory
public class BeanFactory {
    private final ObjectMapper mapper;
    private final String arangoHost;
    private final int arangoPort;
    private final String arangoUser;
    private final String arangoPass;
    private final String arangoDatabase;

    public BeanFactory(ObjectMapper mapper,
                       @Value(value = "${arango.host}") String arangoHost,
                       @Value(value = "${arango.port}") int arangoPort,
                       @Value(value = "${arango.user}") String arangoUser,
                       @Value(value = "${arango.db:}") String arangoDatabase,
                       @Value(value = "${arango.pass}") String arangoPass) {
        this.mapper = mapper;
        this.arangoHost = arangoHost;
        this.arangoPass = arangoPass;
        this.arangoPort = arangoPort;
        this.arangoUser = arangoUser;
        this.arangoDatabase = arangoDatabase;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
    }

    @Bean
    public HttpResponse.BodyHandler<Exchange> exchangeBodyHandler() {
        return new CustomBodyHandler<>(mapper, Exchange.class);
    }

    @Bean
    public HttpResponse.BodyHandler<WeatherData> weatherBodyHandler() {
        return new CustomBodyHandler<>(mapper, WeatherData.class);
    }

    @Bean
    public HttpResponse.BodyHandler<GeoData> geoDataBodyHandler() {
        return new CustomBodyHandler<>(mapper, GeoData.class);
    }

    @Bean
    public ArangoDatabase arangoDatabase() {
        ArangoDB accessor = new ArangoDB.Builder()
                .serde(JacksonSerde.create(mapper))
                .host(arangoHost, arangoPort)
                .user(arangoUser)
                .password(arangoPass)
                .build();
        return accessor.db(arangoDatabase);
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
