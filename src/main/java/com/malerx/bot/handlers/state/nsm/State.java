package com.malerx.bot.handlers.state.nsm;

import com.malerx.bot.data.model.OutgoingMessage;

import java.util.Optional;

public interface State {
    Optional<OutgoingMessage> next();
}
