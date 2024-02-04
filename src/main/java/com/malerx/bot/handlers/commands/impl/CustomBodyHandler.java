package com.malerx.bot.handlers.commands.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;

@Singleton
@Slf4j
public class CustomBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final ObjectMapper mapper;
    private final Class<T> tClass;

    public CustomBodyHandler(ObjectMapper mapper, Class<T> tClass) {
        this.mapper = mapper;
        this.tClass = tClass;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        log.debug("apply() -> map response to {}", tClass);
        HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();

        return HttpResponse.BodySubscribers.mapping(
                upstream, (InputStream body) -> {
                    try {
                        return mapper.readValue(body, tClass);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
