package org.telegram.telegrambots.starter.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.bot.TelegramBot;
import org.telegram.telegrambots.starter.handler.HandlerParsingUtil;
import org.telegram.telegrambots.starter.handler.MethodHolder;
import org.telegram.telegrambots.starter.handler.UpdateHandler;
import org.telegram.telegrambots.starter.handler.UpdateType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class UpdateListener {

    private final Map<String, Map<String, List<MethodHolder>>> updateHandlers;

    @EventListener
    public void onUpdate(BotUpdateEvent event) {
        Update update = event.getUpdate();
        TelegramBot bot = event.getBot();
        log.info("[{}] Received update: {}", bot.botName(), update);
        UpdateHolder<?> updateHolder = UpdateUtil.processUpdate(update);


        Map<String, List<MethodHolder>> handlers = updateHandlers.get(bot.botName());

        Stream.concat(
                handlers.get(updateHolder.getType()).stream(),
                handlers.get(UpdateType.UPDATE).stream()
        ).forEach(methodHolder -> {
            Map<String, Object> args = methodHolder.prepareArgs(updateHolder, bot);

            Method method = methodHolder.getMethod();
            MergedAnnotation<Annotation> annotation =
                    HandlerParsingUtil.getMethodAnnotation(method, UpdateHandler.class);
            String condition = annotation.getString("condition");
            if (!condition.isEmpty()) {
                ExpressionParser parser = new SpelExpressionParser();
                StandardEvaluationContext context = new StandardEvaluationContext();
                context.setVariables(args);

                Object value = parser.parseExpression(condition).getValue(context);

                if (value instanceof Boolean res && res) {
                    try {
                        methodHolder.invoke(args.values().toArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                try {
                    methodHolder.invoke(args.values().toArray());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
