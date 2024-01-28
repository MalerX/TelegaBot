package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.enums.Step;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.data.repository.TGUserRepository;
import com.malerx.bot.factory.stm.RegisterCarStateFactory;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class StartRegisterCar implements CommandHandler {
    private static final String COMMAND = "/reg_car";
    private final StateRepository stateRepository;
    private final TGUserRepository userRepository;

    public StartRegisterCar(StateRepository stateRepository, TGUserRepository userRepository) {
        this.stateRepository = stateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<OutgoingMessage> handle(@NonNull final Update update) {
        var chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() :
                (update.hasMessage() ? update.getMessage().getChatId() : null);
        if (chatId != null) {
            log.debug("handle() -> init process registration car for user {}", chatId);
            boolean exist = userRepository.existsById(chatId);
            if (!exist)
                return Optional.of(createMsg(chatId, """
                        Вы ещё не зарегистрированы. Пройдите регистрацию по команде
                        */registration*"""));
            stateRepository.save(createState(chatId));
            return Optional.of(createMsg(chatId,
                    """
                            Введите информацию об автомобиле в следующем формате:
                            *модель
                            цвет
                            номер гос регистрации*
                            """));
        }
        return Optional.empty();
    }

    private PersistState createState(long chatId) {
        return new PersistState()
                .setChatId(chatId)
                .setDescription("Регистрация автомобиля в системе бота")
                .setStateMachine(RegisterCarStateFactory.class.getSimpleName())
                .setStage(Stage.PROCEED)
                .setStep(Step.ONE);
    }

    private OutgoingMessage createMsg(long chatId, String text) {
        return new TextMessage(Set.of(chatId), text);
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }
}
