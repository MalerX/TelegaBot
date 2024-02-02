package com.malerx.bot.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherData {
    @JsonProperty("fact")
    private Fact fact;

    @JsonProperty("forecast")
    private Forecast forecast;

    @JsonProperty("info")
    private Info info;

    @JsonProperty("now")
    private long now;

    @JsonProperty("now_dt")
    private String nowDt;
}

@Data
class Fact {
    @JsonProperty("condition")
    private String condition;

    @JsonProperty("daytime")
    private String daytime;

    @JsonProperty("feels_like")
    private int feelsLike;

    @JsonProperty("humidity")
    private int humidity;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("obs_time")
    private long obsTime;

    @JsonProperty("polar")
    private boolean polar;

    @JsonProperty("pressure_mm")
    private int pressureMm;

    @JsonProperty("pressure_pa")
    private int pressurePa;

    @JsonProperty("season")
    private String season;

    @JsonProperty("temp")
    private int temp;

    @JsonProperty("wind_dir")
    private String windDir;

    @JsonProperty("wind_gust")
    private double windGust;

    @JsonProperty("wind_speed")
    private double windSpeed;
}

@Data
class Forecast {
    @JsonProperty("date")
    private String date;

    @JsonProperty("date_ts")
    private long dateTs;

    @JsonProperty("moon_code")
    private int moonCode;

    @JsonProperty("moon_text")
    private String moonText;

    @JsonProperty("parts")
    private List<WeatherPart> parts;

    @JsonProperty("sunrise")
    private String sunrise;

    @JsonProperty("sunset")
    private String sunset;

    @JsonProperty("week")
    private int week;
}

@Data
class WeatherPart {
    @JsonProperty("condition")
    private String condition;

    @JsonProperty("daytime")
    private String daytime;

    @JsonProperty("feels_like")
    private int feelsLike;

    @JsonProperty("humidity")
    private int humidity;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("part_name")
    private String partName;

    @JsonProperty("polar")
    private boolean polar;

    @JsonProperty("prec_mm")
    private double precMm;

    @JsonProperty("prec_period")
    private int precPeriod;

    @JsonProperty("prec_prob")
    private int precProb;

    @JsonProperty("pressure_mm")
    private int pressureMm;

    @JsonProperty("pressure_pa")
    private int pressurePa;

    @JsonProperty("temp_avg")
    private int tempAvg;

    @JsonProperty("temp_max")
    private int tempMax;

    @JsonProperty("temp_min")
    private int tempMin;

    @JsonProperty("wind_dir")
    private String windDir;

    @JsonProperty("wind_gust")
    private double windGust;

    @JsonProperty("wind_speed")
    private double windSpeed;
}

@Data
class Info {
    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    @JsonProperty("url")
    private String url;
}
