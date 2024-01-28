package com.malerx.bot.handlers.state.nsm.pass;

import com.malerx.bot.data.entity.Car;
import com.malerx.bot.data.entity.PersistState;
import com.malerx.bot.data.entity.TGUser;
import com.malerx.bot.data.enums.Role;
import com.malerx.bot.data.enums.Step;
import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.PassMessage;
import com.malerx.bot.data.repository.StateRepository;
import com.malerx.bot.data.repository.TGUserRepository;
import com.malerx.bot.handlers.state.nsm.State;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Singleton
@Slf4j
public class FirstStepGettingPassState implements State {
    private final Update update;
    private final PersistState state;
    private final TGUserRepository userRepository;
    private final StateRepository stateRepository;

    public FirstStepGettingPassState(Update update,
                                     PersistState state,
                                     TGUserRepository userRepository,
                                     StateRepository stateRepository) {
        this.update = update;
        this.state = state;
        this.userRepository = userRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    public Optional<OutgoingMessage> next() {
        if (update.hasMessage()) {
            Optional<TGUser> opt = userRepository.findById(update.getMessage().getChatId());
            if (opt.isEmpty()) {
                log.warn("next() -> user not found");
                return Optional.empty();
            }
            TGUser user = opt.get();
            var car = createCar(update.getMessage().getText());
            user.getTenant().getCars().add(car);
            userRepository.update(user);
            updateState();
            return Optional.of(sendRequest(user));
        }
        log.warn("next() -> update not contain message");
        return Optional.empty();
    }

    private Car createCar(String msg) {
        String[] carInfo = msg.split("\n");
        if (carInfo.length != 2) {
            log.error("createCar() -> wrong auto data format");
            throw new RuntimeException();
        }
        log.debug("createCar() -> crete car from {}", Arrays.toString(carInfo));

        return new Car()
                .setModel(carInfo[0])
                .setRegNumber(carInfo[1])
                .setActive(Boolean.FALSE);
    }

    private OutgoingMessage sendRequest(TGUser user) {
        Collection<TGUser> users = userRepository.findByRole(Role.OPERATOR);
        var opId = users.stream().findFirst()
                .map(TGUser::getId)
                .orElseThrow();
        return new PassMessage(Set.of(user.getId()), user, opId, state);
    }

    private void updateState() {
        state.setStep(Step.TWO);
        state.setDescription("Ожидание одобрения заявки оператором");
        stateRepository.update(state);
    }
}
