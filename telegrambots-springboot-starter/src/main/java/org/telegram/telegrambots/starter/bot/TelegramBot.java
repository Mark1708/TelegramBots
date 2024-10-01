package org.telegram.telegrambots.starter.bot;

import org.telegram.telegrambots.meta.generics.TelegramClient;

public record TelegramBot(
        String botName,
        TelegramClient telegramClient
) {}
