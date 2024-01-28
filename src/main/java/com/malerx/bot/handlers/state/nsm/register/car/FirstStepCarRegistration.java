package com.malerx.bot.handlers.state.nsm.register.car;

import com.malerx.bot.data.entity.Car;
import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.entity.TGUser;
import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.enums.Step;
import com.malerx.bot.data.model.ButtonMessage;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.CarRepository;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.data.repository.TGUserRepository;
import com.malerx.bot.handlers.state.nsm.State;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Slf4j
public class FirstStepCarRegistration implements State {
    private static final String YES = "Yes";
    private static final String NO = "No";

    private final Message message;
    private final PersistState state;

    private final TGUserRepository userRepository;
    private final StateRepository stateRepository;
    private final CarRepository carRepository;

    public FirstStepCarRegistration(Update update,
                                    PersistState state,
                                    TGUserRepository userRepository,
                                    StateRepository stateRepository,
                                    CarRepository carRepository) {
        this.message = update.getMessage();
        this.userRepository = userRepository;
        this.stateRepository = stateRepository;
        this.state = state;
        this.carRepository = carRepository;
    }

    @Override
    public Optional<OutgoingMessage> next() {
        log.debug("next() -> first step register car");
        Optional<TGUser> user = findUser(message.getChatId());
        if (user.isEmpty())
            return userNotFound();
        Optional<Car> car = createCar();
        if (car.isEmpty())
            return wrongFormat();
        return car.map(c -> addCar(user.get(), c))
                .orElseGet(this::wrongFormat);
    }

    private Optional<TGUser> findUser(Long id) {
        log.debug("findUser() -> find user with id {}", id);
        return userRepository.findById(id);
    }

    private Optional<Car> createCar() {
        String[] carIfo = message.getText().split("\n");
        if (carIfo.length != 3) {
            log.error("createCar() -> wrong auto data format");
            return Optional.empty();
        }
        log.debug("createCar() -> crete car from {}", Arrays.toString(carIfo));

        var car = new Car()
                .setModel(carIfo[0])
                .setColor(carIfo[1])
                .setRegNumber(carIfo[2]);
        return Optional.of(carRepository.save(car));
    }

    private Optional<OutgoingMessage> addCar(TGUser user, Car car) {
        var tenant = user.getTenant();
        Set<Car> cars = new HashSet<>(user.getTenant().getCars());
        cars.add(car);
        tenant.setCars(cars);
        state.setStep(Step.TWO)
                .setDescription("Подтверждение ввода");
        userRepository.update(user);
        stateRepository.update(state);
        var msg = new ButtonMessage("""
                Всё верно?%s""".formatted(car.toString()),
                Set.of(message.getChatId()),
                createKeyboard(car.getId()));
        return Optional.of(msg);
    }

    private ReplyKeyboard createKeyboard(Long carId) {
        var approve = InlineKeyboardButton.builder()
                .text("Да")
                .callbackData(YES)
                .build();
        var decline = InlineKeyboardButton.builder()
                .text("Нет")
                .callbackData(carId.toString())
                .build();
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(approve, decline))
                .build();
    }

    private Optional<OutgoingMessage> wrongFormat() {
        return Optional.of(new TextMessage(Set.of(message.getChatId()), """
                Введённые данные не соответствуют ожидаемому формату. Проверьте \
                вводимые данные"""));
    }

    private Optional<OutgoingMessage> userNotFound() {
        state.setStage(Stage.ERROR)
                .setDescription("""
                        Пользователь %d не зарегистрирован"""
                        .formatted(message.getChatId()));
        PersistState updated = stateRepository.update(state);
        return Optional.of(new TextMessage(
                Set.of(message.getChatId()),
                updated.getDescription()));
    }
}