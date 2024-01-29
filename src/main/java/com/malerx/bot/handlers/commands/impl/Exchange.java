package com.malerx.bot.handlers.commands.impl;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class Exchange {
    String disclaimer;
    LocalDate date;
    long timestamp;
    String base;
    Map<String, Double> rates;
}
