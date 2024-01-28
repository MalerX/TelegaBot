package com.malerx.bot.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.model.CallbackData;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.factory.stm.StateFactory;
import com.malerx.bot.handlers.commands.CommandHandler;
import io.micronaut.core.annotation.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class HandlerManager {
    private final Collection<CommandHandler> commands;
    private final Map<String, StateFactory> stateFactories;
    private final StateRepository stateRepository;
    private final ObjectMapper mapper;

    public HandlerManager(Collection<CommandHandler> commands,
                          Collection<StateFactory> stateFactories,
                          StateRepository stateRepository,
                          ObjectMapper mapper) {
        this.commands = commands;
        this.stateRepository = stateRepository;
        this.stateFactories = stateFactories.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        (k) -> k.getClass().getSimpleName(),
                        (v) -> v)
                );
        this.mapper = mapper;
    }

    public Optional<OutgoingMessage> handle(@NonNull Update update) {
        if (update.hasMessage() && update.getMessage().getText().startsWith("/")) {
            return commandHandling(update);
        } else {
            return findState(update);
        }
    }

    private Optional<OutgoingMessage> commandHandling(@NonNull Update update) {
        for (CommandHandler handler :
                commands) {
            if (handler.support(update)) {
                return handler.handle(update);
            }
        }
        log.error("commandHandling() -> not found handler for {} command", update.getMessage().getText());
        return Optional.empty();
    }

    private CallbackData parseCallbackDate(final Update update) {
        try {
            return mapper.readValue(update.getCallbackQuery().getData(), CallbackData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<OutgoingMessage> findState(Update update) {
        if (update.hasCallbackQuery()) {
            var callback = parseCallbackDate(update);
            Optional<PersistState> state = stateRepository.findById(callback.getStateId());
            if (state.isPresent()) {
                return stateHandling(state.get(), update);
            }
            log.error("findState() -> not found state {}", callback.getStateId());
            return notFoundState(update.getCallbackQuery().getFrom().getId());
        } else {
            var chatId = update.getMessage().getChatId();
            Collection<PersistState> activeProcesses = stateRepository.findActiveProcess(chatId, Stage.PROCEED);
            if (activeProcesses != null) {
                if (activeProcesses.size() > 1)
                    log.warn("findState() -> for user {} found more then 1 state", chatId);
                return stateHandling(activeProcesses.iterator().next(), update);
            }
            log.error("findState() -> not found state for user {}", chatId);
            return notFoundState(chatId);
        }
    }

    private Optional<OutgoingMessage> stateHandling(final PersistState state, final Update update) {
        log.debug("stateHandling() -> handling state {}", state);
        var factory = stateFactories.get(state.getStateMachine());
        if (Objects.nonNull(factory)) {
            var stateMachine = factory.createState(state, update);
            return stateMachine.next();
        }
        return sendError(state);
    }

    private Optional<OutgoingMessage> sendError(PersistState s) {
        s.setStage(Stage.ERROR);
        s.setDescription(
                String.format("Не найдена реализованная машина состояний для %s", s.getStateMachine())
        );
        stateRepository.update(s);
        return Optional.of(new TextMessage(Set.of(s.getChatId()), s.getDescription()));
    }

    private Optional<OutgoingMessage> notFoundState(final Long chatId) {
        log.warn("commandHandling() -> fail handle update from user {}", chatId);
        var msg = new TextMessage(Set.of(chatId),
                """
                        У вас нет начатых/незавершённых процессов.
                        Чтобы ознакомиться c доступными услугами введите
                                               
                        \t\t\t*/help*""");
        return Optional.of(msg);
    }
}
