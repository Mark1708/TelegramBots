package org.telegram.telegrambots.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.starter.bot.BotsHolder;
import org.telegram.telegrambots.starter.bot.TelegramBot;
import org.telegram.telegrambots.starter.bot.TelegramBotInitializer;
import org.telegram.telegrambots.starter.bot.longpolling.LongPollingBotsApplication;
import org.telegram.telegrambots.starter.bot.longpolling.SpringLongPollingBot;
import org.telegram.telegrambots.starter.bot.properties.TelegramBotsProperties;
import org.telegram.telegrambots.starter.bot.webhook.SpringTelegramWebhookBot;
import org.telegram.telegrambots.starter.bot.webhook.WebhookBotsApplication;
import org.telegram.telegrambots.starter.contoller.BotController;
import org.telegram.telegrambots.starter.handler.HandlerParsingUtil;
import org.telegram.telegrambots.starter.handler.MethodHolder;
import org.telegram.telegrambots.starter.handler.UpdateHandler;
import org.telegram.telegrambots.starter.update.BotUpdateEvent;
import org.telegram.telegrambots.starter.update.UpdateListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@ConditionalOnProperty(
        prefix = "telegrambots",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(
        value = {TelegramBotsProperties.class}
)
public class TelegramBotStarterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UpdateListener updateListener(
            Map<String, Map<String, List<MethodHolder>>> updateHandlers
    ) {
        return new UpdateListener(updateHandlers);
    }

    @Bean
    @ConditionalOnMissingBean(BotsHolder.class)
    public BotsHolder botsHolder(
            TelegramBotsProperties telegramBotsProperties,
            ApplicationEventPublisher eventPublisher
    ) {
        List<SpringTelegramWebhookBot> webhookBots = new ArrayList<>();
        List<SpringLongPollingBot> longPollingBots = new ArrayList<>();
        telegramBotsProperties.getBots().forEach((botName, botProperties) -> {
            TelegramClient telegramClient = new OkHttpTelegramClient(botProperties.getToken());
            TelegramBot bot = new TelegramBot(botName, telegramClient);

            switch (botProperties.getType()) {
                case WEBHOOK:
                    webhookBots.add(new SpringTelegramWebhookBot(
                            botName,
                            (update) -> {
                                eventPublisher.publishEvent(
                                        new BotUpdateEvent(this, bot, update)
                                );
                                return null;
                            },
                            () -> {
                                try {
                                    telegramClient.execute(SetWebhook
                                            .builder()
                                            .url(botProperties.getWebhookUrl() + "/" + botName)
                                            .build());
                                } catch (TelegramApiException e) {
                                    log.info("Error setting webhook");
                                }
                            },
                            () -> {
                                try {
                                    telegramClient.execute(new DeleteWebhook());
                                } catch (TelegramApiException e) {
                                    log.info("Error deleting webhook");
                                }
                            }
                    ));
                    break;
                case LONG_POOLING:
                    longPollingBots.add(new SpringLongPollingBot() {
                        @Override
                        public String getBotToken() {
                            return botProperties.getToken();
                        }

                        @Override
                        public LongPollingUpdateConsumer getUpdatesConsumer() {
                            return (LongPollingSingleThreadUpdateConsumer) update -> {
                                eventPublisher.publishEvent(
                                        new BotUpdateEvent(this, bot, update)
                                );
                            };
                        }
                    });
            }
        });

        return new BotsHolder(
                webhookBots,
                longPollingBots
        );
    }

    @Bean
    @ConditionalOnMissingBean(WebhookBotsApplication.class)
    public WebhookBotsApplication telegramBotsWebhookApplication() throws TelegramApiException {
        return new WebhookBotsApplication();
    }

    @Bean
    @ConditionalOnMissingBean(LongPollingBotsApplication.class)
    public LongPollingBotsApplication telegramBotsLongPollingApplication() {
        return new LongPollingBotsApplication();
    }

    @Bean("webhookBotTelegramBotInitializer")
    @ConditionalOnMissingBean(name = "webhookBotTelegramBotInitializer")
    public TelegramBotInitializer<WebhookBotsApplication, SpringTelegramWebhookBot> webhookBotTelegramBotInitializer(
            WebhookBotsApplication telegramBotsApplication,
            ObjectProvider<List<SpringTelegramWebhookBot>> webhookBots,
            BotsHolder botsHolder
    ) {
        return new TelegramBotInitializer<>(
                telegramBotsApplication,
                Stream.concat(
                        webhookBots.getIfAvailable(Collections::emptyList).stream(),
                        botsHolder.webhookBots().stream()
                ).toList()
        );
    }

    @Bean("longPollingTelegramBotInitializer")
    @ConditionalOnMissingBean(name = "longPollingTelegramBotInitializer")
    public TelegramBotInitializer<LongPollingBotsApplication, SpringLongPollingBot> longPollingTelegramBotInitializer(
            LongPollingBotsApplication telegramBotsApplication,
            ObjectProvider<List<SpringLongPollingBot>> longPollingBots,
            BotsHolder botsHolder
    ) {
        return new TelegramBotInitializer<>(
                telegramBotsApplication,
                Stream.concat(
                        longPollingBots.getIfAvailable(Collections::emptyList).stream(),
                        botsHolder.longPollingBots().stream()
                ).toList()
        );
    }

    @Bean("botControllers")
    @ConditionalOnMissingBean(name = "botControllers")
    public Map<String, List<Object>> botControllers(ApplicationContext context) {
        return new ArrayList<>(context.getBeansWithAnnotation(BotController.class).values())
                .stream()
                .collect(Collectors.groupingBy(controller -> controller.getClass()
                        .getAnnotation(BotController.class).value())
                );
    }

    @Bean("updateHandlers")
    @ConditionalOnMissingBean(name = "updateHandlers")
    public Map<String, Map<String, List<MethodHolder>>> updateHandlers(
            Map<String, List<Object>> botControllers
    ) {
        return botControllers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> HandlerParsingUtil.getHandlersMap(
                                entry.getValue(), UpdateHandler.class,
                                annotation -> annotation.getString("type")
                        )
                ));
    }
}
