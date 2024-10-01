package org.telegram.telegrambots.starter.bot.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class BotProperties {

    @NotNull
    private BotType type;

    @NotEmpty
    private String username;
    @NotEmpty
    private String token;

    private String webhookUrl;
}
