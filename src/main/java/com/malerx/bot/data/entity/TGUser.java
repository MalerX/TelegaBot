package com.malerx.bot.data.entity;

import com.malerx.bot.data.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TGUser {
    @Id
    private Long id;
    private String nickname;
    private Role role;
    @OneToOne(cascade = CascadeType.ALL)
    private Tenant tenant;
}
