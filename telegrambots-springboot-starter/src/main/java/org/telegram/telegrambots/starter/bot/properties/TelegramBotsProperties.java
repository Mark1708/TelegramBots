package org.telegram.telegrambots.starter.bot.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "telegrambots")
public class TelegramBotsProperties {

    private Boolean enabled;
    private Map<String, BotProperties> bots = new HashMap<>();
}
