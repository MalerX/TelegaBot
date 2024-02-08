package com.malerx.bot.services.weather;

import com.arangodb.ArangoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.services.exchange.Exchange;
import com.malerx.bot.storage.AbstractCache;

import javax.inject.Singleton;

@Singleton
public class CurrencyCache extends AbstractCache<Exchange> {
    public CurrencyCache(ArangoDatabase database, ObjectMapper mapper) {
        super(database, mapper, Exchange.class);
    }
}
