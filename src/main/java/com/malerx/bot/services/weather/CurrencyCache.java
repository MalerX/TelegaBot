package com.malerx.bot.services.weather;

import com.arangodb.ArangoDatabase;
import com.malerx.bot.services.exchange.Exchange;
import com.malerx.bot.storage.AbstractCache;

import javax.inject.Singleton;

@Singleton
public class CurrencyCache extends AbstractCache<Exchange> {
    public CurrencyCache(ArangoDatabase database) {
        super(database, Exchange.class);
    }
}
