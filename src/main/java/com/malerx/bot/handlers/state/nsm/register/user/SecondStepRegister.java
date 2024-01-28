package com.malerx.bot.handlers.state.nsm.register.user;

import com.malerx.bot.data.entity.Address;
import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.entity.TGUser;
import com.malerx.bot.data.enums.Stage;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.data.repository.TGUserRepository;
import com.malerx.bot.handlers.state.nsm.State;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class SecondStepRegister implements State {
    private final Message message;
    private final PersistState state;

    private final StateRepository stateRepository;
    private final TGUserRepository userRepository;

    public SecondStepRegister(Update update,
                              PersistState state,
                              StateRepository stateRepository,
                              TGUserRepository userRepository) {
        this.message = update.getMessage();
        this.state = state;
        this.stateRepository = stateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<OutgoingMessage> next() {
        Optional<TGUser> user = userRepository.findById(message.getChatId());
        if (user.isEmpty())
            return alreadyRegistered();

        var address = createAddress();
        if (address.isPresent())
            return updateUser(user.get(), address.get());

        log.error("two() -> fail create address");
        return Optional.of(createMessage("""
                Ошибка при создании адреса. Проверьте введённые данные"""));
    }

    private Optional<OutgoingMessage> alreadyRegistered() {
        state.setStage(Stage.ERROR)
                .setDescription("Не найдет пользователь");
        var msg = createMessage("""
                Не найден пользователь с ID %d"""
                .formatted(message.getChatId()));
        stateRepository.update(state);
        return Optional.of(msg);
    }

    private Optional<OutgoingMessage> updateUser(TGUser user, Address address) {
        log.debug("updateTgUser() -> update user {}", user.getId());
        user.getTenant().setAddress(address);
        userRepository.update(user);
        state.setStage(Stage.DONE);
        var msg = createMessage("""
                Спасибо за регистрацию, теперь вам доступны \
                дополнительные опции бота""");
        stateRepository.update(state);
        return Optional.of(msg);
    }

    private Optional<Address> createAddress() {
        log.debug("createAddress() -> create Address from {}", message.getText());
        var streetBuildNumber = message.getText().split("\n");
        if (streetBuildNumber.length == 3) {
            return Optional.of(new Address()
                    .setStreet(streetBuildNumber[0])
                    .setBuild(streetBuildNumber[1])
                    .setApartment(streetBuildNumber[2]));
        }
        {
            log.error("createAddress() -> wrong input text: {}", message.getText());
            return Optional.empty();
        }
    }

    private TextMessage createMessage(String text) {
        return new TextMessage(Set.of(message.getChatId()), text);
    }
}
