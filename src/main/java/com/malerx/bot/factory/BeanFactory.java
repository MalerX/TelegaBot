package com.malerx.bot.factory;

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

    public BeanFactory(ObjectMapper mapper,
                       @Value(value = "${api.vk.access_token}") String vkToken,
                       @Value(value = "${api.vk.group_id}") Long groupId) {
        this.mapper = mapper;
        this.vkToken = vkToken;
        this.groupId = groupId;
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
}
