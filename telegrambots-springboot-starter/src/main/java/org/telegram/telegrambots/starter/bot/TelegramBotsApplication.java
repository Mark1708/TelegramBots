package org.telegram.telegrambots.starter.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramBotsApplication<B> {
    void registerBot(B bot) throws TelegramApiException;
}
