package com.malerx.bot.services.exchange;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class Exchange {
    String disclaimer;
    String date;
    long timestamp;
    String base;
    Map<String, Double> rates;
}
