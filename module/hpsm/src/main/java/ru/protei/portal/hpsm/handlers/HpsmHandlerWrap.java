package ru.protei.portal.hpsm.handlers;

import org.reflections.Reflections;
import protei.utils.common.Tuple;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.annotations.CaseHandler;
import ru.protei.portal.hpsm.annotations.Handler;
import ru.protei.portal.hpsm.api.HpsmStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HpsmHandlerWrap {
    private final Map<HpsmStatus, BiConsumer> handlersMap = getHandlers();

    private Map<HpsmStatus, BiConsumer> getHandlers() {
        final Reflections reflections = new Reflections("ru.protei.portal.hpsm.handlers");

        final Stream<Class<? extends BiConsumer>> subtypes = reflections
                .getSubTypesOf(BiConsumer.class)
                .stream();

        return subtypes
                .map(x -> {
                    try {
                        return new Tuple<BiConsumer, Stream<CaseHandler>>(x.newInstance(),
                                Arrays.stream(x.getAnnotation(Handler.class).value()));
                    } catch (InstantiationException | IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(x -> Objects.requireNonNull(x).a != null)
                .flatMap(x -> (Objects.requireNonNull(x).b).map(y -> new Tuple<>(y.hpsmStatus(), x.a)))
                .collect(Collectors.toMap(t -> t.a, t -> t.b));
    }

    public void handle(HpsmStatus status, CaseComment comment, CaseObject obj) {
        handlersMap.get(status).accept(comment, obj);
    }
}
