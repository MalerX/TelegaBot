package com.malerx.bot.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    private String street;
    private String build;
    private String apartment;

    @Override
    public String toString() {
        return this.build + "/" + this.apartment;
    }
}
