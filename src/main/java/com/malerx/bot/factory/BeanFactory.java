package com.malerx.bot.factory;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.serde.jackson.JacksonSerde;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static io.vertx.core.json.jackson.DatabindCodec.mapper;

@Factory
public class BeanFactory {
    private final String arangoHost;
    private final int arangoPort;
    private final String arangoUser;
    private final String arangoPass;
    private final String arangoDatabase;
    private final ObjectMapper mapper;

    public BeanFactory(@Value(value = "${arango.host}") String arangoHost,
                       @Value(value = "${arango.port}") int arangoPort,
                       @Value(value = "${arango.user}") String arangoUser,
                       @Value(value = "${arango.db:}") String arangoDatabase,
                       @Value(value = "${arango.pass}") String arangoPass,
                       ObjectMapper mapper) {
        this.arangoHost = arangoHost;
        this.arangoPass = arangoPass;
        this.arangoPort = arangoPort;
        this.arangoUser = arangoUser;
        this.arangoDatabase = arangoDatabase;
        this.mapper = mapper;
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
                .serde(JacksonSerde.create(mapper()))
                .host(arangoHost, arangoPort)
                .user(arangoUser)
                .password(arangoPass)
                .build();
        return accessor.db(arangoDatabase);
    }
}
