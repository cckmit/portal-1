package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return (null == collection || collection.isEmpty());
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty( Iterable iterable ) {
        return (null == iterable || !iterable.iterator().hasNext());
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

    public static <T> T find(Collection<T> col, Predicate<T> predicate) {
        return col.stream().filter(predicate).findAny().orElse(null);
    }

    public static <T> int size( Collection<T> col) {
        return col == null ? 0 : col.size();
    }
}
