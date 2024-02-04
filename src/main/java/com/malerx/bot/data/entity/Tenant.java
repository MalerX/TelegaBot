package com.malerx.bot.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Tenant {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String surname;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Override
    public String toString() {
        return this.surname + " " + this.name;
    }
}
