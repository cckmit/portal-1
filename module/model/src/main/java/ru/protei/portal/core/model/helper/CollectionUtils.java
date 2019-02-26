package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
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

    public static <T> T getFirst( Iterable<T> iterable ) {
        return isEmpty( iterable ) ? null : iterable.iterator().next();
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

    public static <I, O> void transform( final Iterable<I> iterable, final Collection<O> output,
                                         final Function<? super I, ? extends O> mapper ) {
        if ( iterable == null || mapper == null || output == null ) {
            return;
        }

        Iterator<I> it = iterable.iterator();
        while (it.hasNext()) {
            output.add( mapper.apply( it.next() ) );
        }
    }

    public static <I, O> void transform( final Iterable<I> iterable, final Collection<O> output,
                                         final BiConsumer<? super I, Consumer<O>> mapper ) {
        if ( iterable == null || mapper == null || output == null ) {
            return;
        }

        Consumer<O> consumer = o -> output.add( o );

        Iterator<I> it = iterable.iterator();
        while (it.hasNext()) {
            mapper.accept( it.next(), consumer );
        }
    }

    public static <T> T find(Collection<T> col, Predicate<T> predicate) {
        return col.stream().filter(predicate).findAny().orElse(null);
    }

    public static <T> int size( Collection<T> col) {
        return col == null ? 0 : col.size();
    }

    public static <R, T> Set<R> toSet( Iterable<T> iterable, Function<? super T, ? extends R> mapper ) {
        Set<R> result = new HashSet<>();
        transform( iterable, result, mapper );
        return result;
    }

    public static <R, T> Set<R> toSet( Iterable<T> iterable, BiConsumer<? super T, Consumer<R>> consumer ) {
        Set<R> result = new HashSet<>();
        transform( iterable, result, consumer );
        return result;
    }

    public static <T, K, U> Map<K, U> toMap( final Iterable<T> iterable,
                                             Function<? super T, ? extends K> keyMapper,
                                             Function<? super T, ? extends U> valueMapper ) {
        HashMap<K, U> result = new HashMap<>();
        if (iterable == null || keyMapper == null || valueMapper == null) {
            return result;
        }

        Iterator<T> it = iterable.iterator();
        while (it.hasNext()) {
            T next = it.next();
            result.put( keyMapper.apply( next ), valueMapper.apply( next ) );
        }
        return result;
    }
}
