package com.malerx.bot.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.handlers.commands.impl.CustomBodyHandler;
import com.malerx.bot.services.exchange.Exchange;
import com.malerx.bot.services.weather.GeoData;
import com.malerx.bot.services.weather.WeatherData;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;

@Factory
public class BeanFactory {
    private final ObjectMapper mapper;

    public BeanFactory(ObjectMapper mapper) {
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
}
