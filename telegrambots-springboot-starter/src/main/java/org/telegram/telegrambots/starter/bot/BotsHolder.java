package org.telegram.telegrambots.starter.bot;

import org.telegram.telegrambots.starter.bot.longpolling.SpringLongPollingBot;
import org.telegram.telegrambots.starter.bot.webhook.SpringTelegramWebhookBot;

import java.util.List;

public record BotsHolder(
        List<SpringTelegramWebhookBot> webhookBots,
        List<SpringLongPollingBot> longPollingBots
) {}
