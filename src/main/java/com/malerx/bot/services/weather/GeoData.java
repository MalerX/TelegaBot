package com.malerx.bot.services.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GeoData {
    @JsonProperty("response")
    private Response response;

    public Coordinates getCoordinates() {
        String pos = response.getGeoObjectCollection()
                .getFeatureMembers().getFirst()
                .getGeoObject().getPoint().getPos();
        String city = response.getGeoObjectCollection()
                .getFeatureMembers().getFirst().getGeoObject().getName();
        return new Coordinates(city, pos);
    }
}

@Data
class Response {
    @JsonProperty("GeoObjectCollection")
    private GeoObjectCollection geoObjectCollection;
}

@Data
class GeoObjectCollection {
    @JsonProperty("featureMember")
    private List<FeatureMember> featureMembers;

    @JsonProperty("metaDataProperty")
    private MetaDataProperty metaDataProperty;
}

@Data
class FeatureMember {
    @JsonProperty("GeoObject")
    private GeoObject geoObject;
}

@Data
class GeoObject {
    @JsonProperty("Point")
    private Point point;

    @JsonProperty("boundedBy")
    private BoundedBy boundedBy;

    @JsonProperty("description")
    private String description;

    @JsonProperty("metaDataProperty")
    private MetaDataProperty metaDataProperty;

    @JsonProperty("name")
    private String name;

    @JsonProperty("uri")
    private String uri;
}

@Data
class Point {
    @JsonProperty("pos")
    private String pos;
}

@Data
class BoundedBy {
    @JsonProperty("Envelope")
    private Envelope envelope;
}

@Data
class Envelope {
    @JsonProperty("lowerCorner")
    private String lowerCorner;

    @JsonProperty("upperCorner")
    private String upperCorner;
}

@Data
class MetaDataProperty {
    @JsonProperty("GeocoderMetaData")
    private GeocoderMetaData geocoderMetaData;

    @JsonProperty("GeocoderResponseMetaData")
    private GeocoderResponseMetaData geocoderResponseMetaData;
}

@Data
class GeocoderMetaData {
    @JsonProperty("Address")
    private Address address;

    @JsonProperty("AddressDetails")
    private AddressDetails addressDetails;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("precision")
    private String precision;

    @JsonProperty("text")
    private String text;
}

@Data
class Address {
    @JsonProperty("Components")
    private List<Component> components;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("formatted")
    private String formatted;
}

@Data
class Component {
    @JsonProperty("kind")
    private String kind;

    @JsonProperty("name")
    private String name;
}

@Data
class AddressDetails {
    @JsonProperty("Country")
    private Country country;
}

@Data
class Country {
    @JsonProperty("AddressLine")
    private String addressLine;

    @JsonProperty("AdministrativeArea")
    private AdministrativeArea administrativeArea;

    @JsonProperty("CountryName")
    private String countryName;

    @JsonProperty("CountryNameCode")
    private String countryNameCode;
}

@Data
class AdministrativeArea {
    @JsonProperty("AdministrativeAreaName")
    private String administrativeAreaName;
}

@Data
class GeocoderResponseMetaData {
    @JsonProperty("boundedBy")
    private Envelope boundedBy;

    @JsonProperty("found")
    private String found;

    @JsonProperty("request")
    private String request;

    @JsonProperty("results")
    private String results;
}