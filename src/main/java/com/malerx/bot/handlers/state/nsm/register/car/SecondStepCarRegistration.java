package com.malerx.bot.handlers.state.nsm.register.car;

import com.malerx.bot.data.entity.Car;
import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.enums.Step;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.CarRepository;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.handlers.state.nsm.State;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class SecondStepCarRegistration implements State {
    private static final String YES = "Yes";

    private final Message message;
    private final CallbackQuery callbackQuery;
    private final PersistState state;

    private final StateRepository stateRepository;
    private final CarRepository carRepository;

    public SecondStepCarRegistration(Update update,
                                     PersistState state,
                                     StateRepository stateRepository,
                                     CarRepository carRepository) {
        if (update.hasCallbackQuery()) {
            this.message = (Message) update.getCallbackQuery().getMessage();
            this.callbackQuery = update.getCallbackQuery();
        } else
            throw new RuntimeException("Wrong input data");

        this.state = state;
        this.stateRepository = stateRepository;
        this.carRepository = carRepository;
    }

    @Override
    public Optional<OutgoingMessage> next() {
        if (Objects.equals(YES, callbackQuery.getData()))
            return ok();
        else
            return rEdit();
    }

    private Optional<OutgoingMessage> ok() {
        state.setStage(Stage.DONE)
                .setDescription("Ввод корректный. Автомобиль добавлен");
        stateRepository.update(state);
        return Optional.of(new TextMessage(Set.of(message.getChatId()), state.getDescription()));
    }

    private Optional<OutgoingMessage> rEdit() {
        var carId = Long.parseLong(callbackQuery.getData());
        Optional<Car> car = carRepository.findById(carId);
        if (car.isEmpty())
            return Optional.empty();
        disableCar(car.get());
        return updateState();
    }

    private Optional<OutgoingMessage> updateState() {
        state.setStep(Step.ONE)
                .setDescription("""
                        Введите информацию об автомобиле в следующем формате:
                        *модель
                        цвет
                        номер гос регистрации*
                        """);
        stateRepository.update(state);
        return Optional.of(new TextMessage(Set.of(message.getChatId()), state.getDescription()));
    }

    private void disableCar(Car c) {
        c.setActive(Boolean.FALSE);
        carRepository.update(c);
        log.debug("disableCar() -> success disabled car {}", c);
    }
}
