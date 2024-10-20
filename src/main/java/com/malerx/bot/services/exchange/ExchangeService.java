package com.malerx.bot.services.exchange;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.storage.AbstractCache;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class ExchangeService {
    private static final String CURRENCY = "currency";
    private final HttpClient client;
    private final URI cbr;
    private final HttpResponse.BodyHandler<Exchange> exchangeBodyHandler;
    private final AbstractCache<Exchange> cache;

    public ExchangeService(HttpClient client,
                           @Value("${api.cbr:}") String uri,
                           HttpResponse.BodyHandler<Exchange> exchangeBodyHandler,
                           AbstractCache<Exchange> cache) {
        this.client = client;
        this.cbr = URI.create(uri);
        this.exchangeBodyHandler = exchangeBodyHandler;
        this.cache = cache;
    }

    public Optional<OutgoingMessage> exchange(Update update) {
        String[] str = update.getMessage().getText().split(" ");
        if (str.length != 3) {
            log.warn("handle() -> wrong response");
            return message(update, "Неверный формат запроса");
        }
        String currency = str[1];
        Double money = Double.parseDouble(str[2]);
        Map<String, Double> currencies = getExchange().map(Exchange::getRates)
                .orElse(Map.of());
        if (currency.isEmpty())
            return message(update, "Не удалось загрузить курсы валют");
        Double currentCourse = currencies.getOrDefault(currency.toUpperCase(), 0.0);
        Double exchanged = money * currentCourse;
        String message = String.format("%.2f RUB = %.2f %s", money, exchanged, currency);
        return message(update, message);
    }

    private Optional<Exchange> getExchange() {
        Optional<Exchange> cached = cache.searchDocument(CURRENCY);
        if (cached.isPresent())
            return cached;
        log.debug("getExchange() -> get exchange course from {}", cbr);
        try {
            HttpRequest request = HttpRequest.newBuilder(cbr).GET().build();
            HttpResponse<Exchange> httpResponse = client.send(request, exchangeBodyHandler);
            cache.saveDocument(httpResponse.body(), CURRENCY);
            return Optional.of(httpResponse.body());
        } catch (IOException | InterruptedException e) {
            log.error("handle() -> error sending request: ", e);
            return Optional.empty();
        }
    }

    private Optional<OutgoingMessage> message(Update update, String message) {
        log.debug("message() -> create outgoing message");
        TextMessage error = new TextMessage(Set.of(update.getMessage().getChatId()), message);
        return Optional.of(error);
    }
}
