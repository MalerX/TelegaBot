package com.malerx.bot.services.weather;

import com.malerx.bot.data.model.OutgoingMessage;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import padeg.lib.Padeg;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class WeatherMessage extends OutgoingMessage {
    private static final DateTimeFormatter output = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final String HEADER_TEMPLATE = "Погода в %s на %s";
    private final WeatherData weather;
    private final String city;

    public WeatherMessage(Set<Long> destination, String city, WeatherData weather) {
        super(destination);
        this.weather = weather;
        this.city = city;
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
        String rodCity = Padeg.getAppointmentPadeg(city, 3);
        LocalDate date = LocalDate.parse(weather.getNowDt().substring(0, 10));
        String header = String.format(HEADER_TEMPLATE, rodCity, output.format(date));
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow(null, header)
                .setPaddingTopBottom(1)
                .setTextAlignment(TextAlignment.CENTER);
        table.addRule();
        AT_Row temp = table.addRow("🌡 Температура:", fact.getTemp());
        temp.getCells().getFirst().getContext().setTextAlignment(TextAlignment.LEFT);
        temp.getCells().getLast().getContext().setTextAlignment(TextAlignment.RIGHT);
        table.addRule();
        AT_Row humidity = table.addRow("🌊 Влажность: ", fact.getHumidity());
        humidity.getCells().getFirst().getContext().setTextAlignment(TextAlignment.LEFT);
        humidity.getCells().getLast().getContext().setTextAlignment(TextAlignment.RIGHT);
        table.addRule();
        AT_Row wind = table.addRow("🌬 Скорость ветра: ", fact.getWindSpeed());
        wind.getCells().getFirst().getContext().setTextAlignment(TextAlignment.LEFT);
        wind.getCells().getLast().getContext().setTextAlignment(TextAlignment.RIGHT);
        table.addRule();
        table.getContext().setWidth(45);
        return table.render();
    }
}
