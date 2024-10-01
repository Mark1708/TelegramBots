package org.telegram.telegrambots.starter.update;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.bot.TelegramBot;

@Getter
public class BotUpdateEvent extends ApplicationEvent {

    private final TelegramBot bot;
    private final Update update;

    public BotUpdateEvent(Object source, TelegramBot bot, Update update) {
        super(source);
        this.bot = bot;
        this.update = update;
    }
}
