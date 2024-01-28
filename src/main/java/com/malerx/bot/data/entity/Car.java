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
public class Car {
    @Id
    @GeneratedValue
    private Long id;
    private String model;
    private String color;
    private String regNumber;
    private Boolean active;

    @Override
    public String toString() {
        return """
                                
                                
                модель: %s
                цвет: %s
                госномер: %s """
                .formatted(this.model, this.color, this.regNumber);
    }
}
