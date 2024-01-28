package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.enums.Step;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.data.repository.TGUserRepository;
import com.malerx.bot.factory.stm.RegisterStateFactory;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class RegisterHandler implements CommandHandler {
    private final static String COMMAND = "/register";

    private final StateRepository stateRepository;
    private final TGUserRepository userRepository;

    public RegisterHandler(StateRepository stateRepository,
                           TGUserRepository userRepository) {
        this.stateRepository = stateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        var message = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage() :
                (update.hasMessage() ? update.getMessage() : null);
        if (message != null) {
            boolean exist = userRepository.existsById(message.getChatId());
            if (exist) {
                log.debug("handle() -> user already register");
                return Optional.of(
                        createMsg(message.getChatId(),
                                """
                                        Вы уже зарегистрированы в системе бота."""));
            } else {
                log.debug("handle() -> handle request of registration");
                return startRegistration(message.getChatId());
            }
        }
        return Optional.empty();
    }

    private Optional<OutgoingMessage> startRegistration(long chatId) {
        PersistState persistState = new PersistState()
                .setChatId(chatId)
                .setStateMachine(RegisterStateFactory.class.getSimpleName())
                .setStep(Step.ONE)
                .setStage(Stage.PROCEED)
                .setDescription("Регистрация пользователя в системе бота");
        stateRepository.save(persistState);
        return Optional.of(createMsg(chatId, "Введите ваши имя и фамилию:"));

    }

    private OutgoingMessage createMsg(long chatId, String s) {
        return new TextMessage(Set.of(chatId), s);
    }

    @Override
    public Boolean support(@NonNull Update update) {
        return update.getMessage().getText().startsWith(COMMAND);
    }
}
