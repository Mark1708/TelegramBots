package org.telegram.telegrambots.starter.handler;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class HandlerParsingUtil {

    private static List<Method> getAnnotatedMethods(
            Class<?> clazz,
            Class<? extends Annotation> annotationClass
    ) {
        return Stream.of(clazz.getMethods())
                .filter(method -> AnnotationUtils.findAnnotation(method, annotationClass) != null)
                .toList();
    }

    public static <T extends Annotation, V> Map<V, List<MethodHolder>> getHandlersMap(
            List<Object> botControllers,
            Class<T> annotationClass,
            Function<MergedAnnotation<Annotation>, V> getKey
    ) {
        return botControllers.stream()
                .reduce(
                        new HashMap<>(),
                        (map, controller) -> {
                            getAnnotatedMethods(
                                    AopProxyUtils.ultimateTargetClass(controller),
                                    annotationClass
                            ).forEach(method -> {
                                MergedAnnotation<Annotation> mergedAnnotation =
                                        getMethodAnnotation(method, annotationClass);

                                V key = getKey.apply(mergedAnnotation);
                                MethodHolder methodHolder = new MethodHolder(controller, method);
                                if (map.containsKey(key)) {
                                    map.get(key).add(methodHolder);
                                } else {
                                    map.put(key, new ArrayList<>(){{add(methodHolder);}});
                                }
                            });
                            return map;
                        },
                        (map1, map2) -> {
                            map1.putAll(map2);
                            return map1;
                        }
                );
    }

    public static <T extends Annotation> MergedAnnotation<Annotation> getMethodAnnotation(
            Method method, Class<T> annotationClass
    ) {
        return MergedAnnotations.from(
                        method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY,
                        RepeatableContainers.none()
                ).stream()
                .filter(MergedAnnotationPredicates.typeIn(annotationClass))
                .filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex))
                .distinct()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Expected annotation - " + annotationClass.getName() +
                                        " for method" + method.getName()
                        )
                );
    }
}
