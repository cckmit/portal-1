package ru.protei.portal.core.model.helper;

import java.util.Collection;
import java.util.stream.Stream;

public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return (null == collection || collection.isEmpty());
    }

    public static <T> Stream<T> stream(Collection collection) {
        return null == collection ? Stream.empty() : collection.stream();
    }

}
