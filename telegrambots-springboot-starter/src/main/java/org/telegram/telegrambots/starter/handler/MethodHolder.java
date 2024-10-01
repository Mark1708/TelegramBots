package org.telegram.telegrambots.starter.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.starter.bot.TelegramBot;
import org.telegram.telegrambots.starter.update.UpdateHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MethodHolder {

    private Object controller;
    private Method method;

    public MethodHolder(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public void invoke(Object... args) throws Exception {
        method.setAccessible(true);
        method.invoke(controller, args);
    }

    public Map<String, Object> prepareArgs(UpdateHolder<?> updateHolder, TelegramBot bot) {
        Map<String, Object> argsMap = new LinkedHashMap<>();
        Parameter[] params = method.getParameters();

        Arrays.stream(params).forEach(param -> {
            if (param.getType().equals(updateHolder.getUpdate().getClass())) {
                argsMap.put(param.getName(), updateHolder.getUpdate());
            } else if (param.getType().equals(Update.class)) {
                argsMap.put(param.getName(), updateHolder.getOrigin());
            } else if (param.getType().equals(User.class)) {
                argsMap.put(param.getName(), updateHolder.getUser().orElse(null));
            } else if (param.getType().equals(TelegramBot.class)) {
                argsMap.put(param.getName(), bot);
            } else {
                argsMap.put(param.getName(), null);
            }
        });
        return argsMap;
    }
}
