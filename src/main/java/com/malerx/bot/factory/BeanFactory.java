package com.malerx.bot.factory;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.handlers.commands.impl.CustomBodyHandler;
import com.malerx.bot.services.exchange.Exchange;
import com.malerx.bot.services.weather.GeoData;
import com.malerx.bot.services.weather.WeatherData;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;

@Factory
public class BeanFactory {
    private final ObjectMapper mapper;
    private final Long groupId;
    private final String vkToken;
    private final String arangoHost;
    private final int arangoPort;
    private final String arangoUser;
    private final String arangoPass;
    private final String arangoDatabase;

    public BeanFactory(ObjectMapper mapper,
                       @Value(value = "${api.vk.access_token}") String vkToken,
                       @Value(value = "${api.vk.group_id}") Long groupId,
                       @Value(value = "${arango.host}") String arangoHost,
                       @Value(value = "${arango.port}") int arangoPort,
                       @Value(value = "${arango.user}") String arangoUser,
                       @Value(value = "${arango.db:}") String arangoDatabase,
                       @Value(value = "${arango.pass}") String arangoPass) {
        this.mapper = mapper;
        this.vkToken = vkToken;
        this.groupId = groupId;
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
    public VkApiClient vkClient() {
        TransportClient transportClient = new HttpTransportClient();
        return new VkApiClient(transportClient);
    }

    @Bean
    public UserActor groupActor() {
        return new UserActor(groupId, vkToken);
    }

    @Bean
    public ArangoDatabase arangoDatabase() {
        ArangoDB accessor = new ArangoDB.Builder()
                .host(arangoHost, arangoPort)
                .user(arangoUser)
                .password(arangoPass)
                .build();
        return accessor.db(arangoDatabase);
    }
}
