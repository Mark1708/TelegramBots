package org.telegram.bots;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.bot.TelegramBot;
import org.telegram.telegrambots.starter.contoller.BotController;
import org.telegram.telegrambots.starter.handler.UpdateHandler;
import org.telegram.telegrambots.starter.handler.UpdateType;

@Slf4j
@BotController(BotConstants.TEST_WH_BOT)
public class TestController {

    @UpdateHandler(condition = "#update.hasMessage()")
    public void updateHandlerWithCondition(Update update, TelegramBot bot) throws TelegramApiException {
        log.info("Received message: {}", update.getMessage());
        bot.telegramClient().execute(
                SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text("Message Updated received").build()
        );
    }

    @UpdateHandler(type = UpdateType.MESSAGE, condition = "#message.getText().startsWith(\"/hello\")")
    public void messageHandler(Message message, User user, TelegramBot bot) throws TelegramApiException {
        log.info("Received message: {}", message);
        bot.telegramClient().execute(
                SendMessage.builder()
                        .chatId(user.getId())
                        .text("/hello command received").build()
        );
    }
}
