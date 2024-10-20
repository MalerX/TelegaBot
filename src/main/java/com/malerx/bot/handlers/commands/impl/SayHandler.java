package com.malerx.bot.handlers.commands.impl;

import com.malerx.bot.data.model.OutgoingMessage;
import com.malerx.bot.data.model.TextMessage;
import com.malerx.bot.handlers.commands.CommandHandler;
import demo.lib.HelloWorld;
import demo.lib.Library;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
public class SayHandler implements CommandHandler {
    @Override
    public Optional<OutgoingMessage> handle(Update update) {
        Library library = new Library();
        Long dest = update.getMessage().getChatId();
        if (library.someLibraryMethod()) {
            String name = update.getMessage().getText().split(" ")[1];
            HelloWorld world = new HelloWorld();
            return message(dest, world.say(name));
        }
        return message(dest, "lib fail");
    }

    private Optional<OutgoingMessage> message(Long dest, String say) {
        return Optional.of(new TextMessage(Set.of(dest), say));
    }

    @Override
    public Boolean support(Update update) {
        return update.getMessage().getText().startsWith("/say");
    }

    @Override
    public String getInfo() {
        return "Тест либы";
    }
}
