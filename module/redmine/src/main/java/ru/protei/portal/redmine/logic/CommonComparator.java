package ru.protei.portal.redmine.logic;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protei.utils.common.Tuple;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.redmine.handlers.MergeHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonComparator {

    private final static Logger logger = LoggerFactory.getLogger(CommonComparator.class);

    private List<Field> diff;

    public List<Field> diff(CaseObject oldObj, CaseObject newObj) {
        diff = Arrays.stream(CaseObject.class.getDeclaredFields())
                .map(x -> {
                    try {
                        return !x.get(oldObj).equals(x.get(newObj)) ? x : null;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        logger.debug("Something went wrong when comparing {} and {}", oldObj, newObj);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return diff;
    }

    public void seekMergers() {
        Reflections ref = new Reflections("ru.protei.portal.redmine.handlers");
        String packageName = "";
        String prefix = "";
        diff.stream()
                .map(x -> new Tuple<Field, String>(x, x.getName()))
                .map(x -> {
                    try {
                        return new Tuple<Field, Class>(x.a, Class.forName(packageName + prefix + x.b));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(x -> new Tuple<Field, Set<Class<?>>>(x.a, ref.getTypesAnnotatedWith((Class<? extends Annotation>) x.b)))
                .map(x -> new Tuple<Field, Set<Object>>(x.a, x.b.stream().map(y -> {
                    try {
                        return y.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toSet()))).forEach(x -> x.b.stream().map(y -> ((MergeHandler) y)));
        //                .collect(Collectors.toList());
    }
}
