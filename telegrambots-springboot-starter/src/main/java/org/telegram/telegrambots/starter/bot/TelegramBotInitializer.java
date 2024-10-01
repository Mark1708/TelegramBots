package org.telegram.telegrambots.starter.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class TelegramBotInitializer<A extends TelegramBotsApplication<B>, B> implements InitializingBean {

    private final A telegramBotsApplication;
    private final List<B> bots;

    public TelegramBotInitializer(A telegramBotsApplication, List<B> bots) {
        Objects.requireNonNull(telegramBotsApplication);
        Objects.requireNonNull(bots);
        this.telegramBotsApplication = telegramBotsApplication;
        this.bots = bots;
    }

    @Override
    public void afterPropertiesSet()  {
        try {
            for (B bot : bots) {
                telegramBotsApplication.registerBot(bot);
                handleAfterRegistrationHook(bot);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleAfterRegistrationHook(Object bot) {
        Stream.of(bot.getClass().getMethods())
                .filter(method -> method.getAnnotation(AfterBotRegistration.class) != null)
                .forEach(method -> handleAnnotatedMethod(bot, method));

    }

    private void handleAnnotatedMethod(Object bot, Method method) {
        try {
            if (method.getParameterCount() > 1) {
                log.warn(
                        "Method {} of Type {} has too many parameters",
                        method.getName(), method.getDeclaringClass().getCanonicalName()
                );
                return;
            }
            if (method.getParameterCount() == 0) {
                method.invoke(bot);
                return;
            }
            log.warn(
                    "Method {} of Type {} has invalid parameter type",
                    method.getName(), method.getDeclaringClass().getCanonicalName()
            );
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error(
                    "Couldn't invoke Method {} of Type {}",
                    method.getName(), method.getDeclaringClass().getCanonicalName()
            );
        }
    }
}
