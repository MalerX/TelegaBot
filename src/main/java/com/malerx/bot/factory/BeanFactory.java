package com.malerx.bot.factory;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import java.net.http.HttpClient;

@Factory
public class BeanFactory {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
