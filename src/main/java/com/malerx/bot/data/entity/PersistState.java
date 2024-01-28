package com.malerx.bot.data.entity;

import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.enums.Step;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class PersistState {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String stateMachine;
    private Stage stage;
    private Step step;
    private String description;
    @DateCreated
    private Date start;
    @DateUpdated
    private Date process;
}
