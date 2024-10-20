package com.malerx.bot.services.weather;

import com.malerx.bot.data.model.OutgoingMessage;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_GridThemes;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class WeatherMessage extends OutgoingMessage {
    private static final DateTimeFormatter output = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final String HEADER_TEMPLATE = "Погода %s на %s";
    private final WeatherData weather;

    public WeatherMessage(Set<Long> destination, WeatherData weather) {
        super(destination);
        this.weather = weather;
    }

    @Override
    public Collection<Object> send() {
        String content = "```\n" + this + "\n```";
        return destination.stream()
                .map(id -> {
                    var m = new SendMessage(id.toString(), content);
                    m.enableMarkdown(true);
                    return m;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        log.debug("createTable() -> build table with weather");
        Fact fact = weather.getFact();
        String city = weather.getCity();
        LocalDate date = Optional.of(weather.getNowDt())
                .map(OffsetDateTime::toLocalDate)
                .orElse(LocalDate.now());
        String header = String.format(HEADER_TEMPLATE, city, output.format(date));
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow(null, header)
                .setTextAlignment(TextAlignment.CENTER);
        table.addRule();
        String tempValue = fact.getTemp() + " °C";
        AT_Row temp = table.addRow("🌡 Температура:", tempValue);
        temp.getCells().getFirst().getContext().setTextAlignment(TextAlignment.LEFT);
        temp.getCells().getLast().getContext().setTextAlignment(TextAlignment.CENTER);
        table.addRule();
        String humValue = fact.getHumidity() + "%";
        AT_Row humidity = table.addRow("🌊 Влажность:", humValue);
        humidity.getCells().getFirst().getContext().setTextAlignment(TextAlignment.LEFT);
        humidity.getCells().getLast().getContext().setTextAlignment(TextAlignment.CENTER);
        table.addRule();
        String windSpeedV = fact.getWindSpeed() + " м/с";
        AT_Row wind = table.addRow("🌬 Скорость ветра:", windSpeedV);
        wind.getCells().getFirst().getContext().setTextAlignment(TextAlignment.LEFT);
        wind.getCells().getLast().getContext().setTextAlignment(TextAlignment.CENTER);
        table.addRule();
        table.getContext().setWidth(45);
        table.getContext().setGridTheme(TA_GridThemes.CONNECTORS);
        return table.render();
    }
}
