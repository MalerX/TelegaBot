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
public class ExchangeBodyHandler implements HttpResponse.BodyHandler<Exchange> {
    private final ObjectMapper mapper;

    public ExchangeBodyHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public HttpResponse.BodySubscriber<Exchange> apply(HttpResponse.ResponseInfo responseInfo) {
        log.debug("apply() -> map response to Exchange");
        HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();

        return HttpResponse.BodySubscribers.mapping(
                upstream, (InputStream body) -> {
                    try {
                        return mapper.readValue(body, Exchange.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
