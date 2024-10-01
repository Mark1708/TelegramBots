package org.telegram.telegrambots.starter.bot;

import org.telegram.telegrambots.starter.bot.webhook.WebhookBotsApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicated that the Method of a Class extending {@link WebhookBotsApplication} will be called after the bot was registered
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterBotRegistration {}
