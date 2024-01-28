package com.malerx.bot.data.entity;

import jakarta.persistence.Column;
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
public class GpgRecord {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "pgp_key")
    private String pgpKey;

    @Override
    public String toString() {
        return this.pgpKey;
    }
}
