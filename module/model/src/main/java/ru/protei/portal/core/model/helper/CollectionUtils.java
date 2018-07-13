package ru.protei.portal.core.model.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return (null == collection || collection.isEmpty());
    }

    public static boolean isEmpty( Iterable iterable ) {
        return (null == iterable || !iterable.iterator().hasNext());
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> Stream<T> stream(Collection<T> collection) {
        return null == collection ? Stream.empty() : collection.stream();
    }

    public static <E> boolean contains( Collection<E> collection, E element ) {
        return null != collection ? collection.contains( element ) : false;
    }

    public static <T> Collection<T> emptyIfNull( Collection<T> collection ) {
        return collection == null ? Collections.<T>emptyList() : collection;
    }

    public static <I, O> void transform( final Collection<I> input, final Collection<O> output,
                                         final Transformer<? super I, ? extends O> transformer ) {
        if ( input == null || transformer == null || output == null ) {
            return;
        }

        for (final I item : input) {
            output.add( transformer.transform( item ) );
        }
    }

    public static <T> T getFirst( Iterable<T> iterable ) {
        return isEmpty( iterable ) ? null : iterable.iterator().next();
    }
}
