package org.telegram.telegrambots.starter.bot.longpolling;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

public interface SpringLongPollingBot {
    String getBotToken();
    LongPollingUpdateConsumer getUpdatesConsumer();
}
