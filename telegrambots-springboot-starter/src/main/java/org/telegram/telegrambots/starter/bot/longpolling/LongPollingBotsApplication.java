package org.telegram.telegrambots.starter.bot.longpolling;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.bot.TelegramBotsApplication;

public class LongPollingBotsApplication
        extends TelegramBotsLongPollingApplication
        implements TelegramBotsApplication<SpringLongPollingBot> {
    @Override
    public void registerBot(SpringLongPollingBot bot)  throws TelegramApiException {
        this.registerBot(bot.getBotToken(), bot.getUpdatesConsumer());
    }
}
