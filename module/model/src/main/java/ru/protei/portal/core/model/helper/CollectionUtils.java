package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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

    public static <T> T get( List<T> col, int elementIndex ) {
        if(col==null) return null;
        if(col.size() <= elementIndex )return null;
        return col.get(elementIndex);
    }

    public static <T> T get( T[] col, int elementIndex ) {
        if(col==null) return null;
        if(col.length <= elementIndex )return null;
        return col[elementIndex];
    }

    public static <T> T getFirst( Iterable<T> iterable ) {
        return isEmpty( iterable ) ? null : iterable.iterator().next();
    }
    public static <T> T last (List<T> list) {
        return lastOrDefault(list, null);
    }

    public static <T> T lastOrDefault (List<T> list, T def) {
        return list == null || list.isEmpty() ? def : list.get(list.size()-1);
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

    public static <T> Set<T> emptyIfNull( Set<T> set ) {
        return set == null ? Collections.<T>emptySet() : set;
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

    public static <I, O> void transform( final I[] iterable, final Collection<O> output,
                                         final Function<? super I, ? extends O> mapper ) {
        if ( iterable == null || mapper == null || output == null ) {
            return;
        }

        for (final I next : iterable) {
            output.add( mapper.apply( next ) );
        }
    }

    public static <T> Optional<T> find(Collection<T> col, Predicate<T> predicate) {
        return stream(col).filter(predicate).findAny();
    }

    public static <T> int size(Collection<T> col) {
        return col == null ? 0 : col.size();
    }

    public static int size(Map<?,?> map) {
        return map == null ? 0 : map.size();
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

    public static <R, T> List<R> toList( Iterable<T> iterable, Function<? super T, ? extends R> mapper  ) {
        List<R> result = new ArrayList<>();
        transform( iterable, result, mapper );
        return result;
    }

    public static <R, T> List<R> toList( Iterable<T> iterable, BiConsumer<? super T, Consumer<R>> consumer ) {
        List<R> result = new ArrayList<>();
        transform( iterable, result, consumer );
        return result;
    }

    public static <R, T> List<R> toList( T[] iterable, Function<? super T, ? extends R> mapper  ) {
        List<R> result = new ArrayList<R>();
        transform( iterable, result, mapper );
        return result;
    }

    public static <T> List<T> filterToList( Iterable<T> iterable, Predicate<? super T> predicate ) {
        List<T> result = new ArrayList<>();
        if(predicate ==null) return result;

        transform( iterable, result, ( t, rConsumer ) -> {
            if(predicate.test( t )){
                rConsumer.accept( t );
            }
        } );
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

    public static <T> List<T> singleValueList(T value) {
        List<T> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    public static <T> List<T> listOf(T... elements){
        if(elements == null) return new ArrayList<>();
        return new ArrayList<>( Arrays.asList( elements ));
    }

    public static <T> List<T> listOf(Collection<T> elements){
        if(elements == null) return new ArrayList<>();
        return new ArrayList<>( elements );
    }

    public static <T> Set<T> setOf(T... elements){
        if(elements == null) return new HashSet<>();
        return new HashSet<>( Arrays.asList( elements ));
    }

    public static <T> Set<T> setOf(Collection<T> elements){
        if(elements == null) return new HashSet<>();
        return new HashSet<>( elements );
    }

    public static <T> T findPreviousElement(Set<T> set, T element) {
        T prevEl = null;
        for (T el : set) {
            if (Objects.equals(el, element)) {
                return prevEl;
            }
            prevEl = el;
        }
        return prevEl;
    }

    public static <T> T findNextElement(Set<T> set, T element) {
        boolean shouldReturn = false;
        for (T el : set) {
            if (shouldReturn) {
                return el;
            }
            if (Objects.equals(el, element)) {
                shouldReturn = true;
            }
        }
        return null;
    }

    /**
     * Сравнение двух коллекций
     *
     * @param <T>    тип элементов, хранящихся в сравниваемых коллекциях
     * @param first  первая коллекция ("старая")
     * @param second вторая коллекция ("новая")
     * @return результат сравнения двух коллекций
     */
    public static <T> DiffCollectionResult<T> diffCollection(Collection<T> first, Collection<T> second) {
        return diffCollection( first, second, null );
    }

    /**
     * Сравнение двух коллекций
     *
     * @param <T>    тип элементов, хранящихся в сравниваемых коллекциях
     * @param first  первая коллекция ("старая")
     * @param second вторая коллекция ("новая")
     * @param comparator сравнение елементов на измененность
     * @return результат сравнения двух коллекций
     */
    public static <T> DiffCollectionResult<T> diffCollection(Collection<T> first, Collection<T> second, Comparator<T> comparator) {
        DiffCollectionResult<T> result = new DiffCollectionResult<T>();
        if (first == null) {
            if (second != null) {
                for (T entry : second) {
                    result.putAddedEntry(entry);
                }
            }
            return result;
        } else if (second == null) {
            for (T entry : first) {
                result.putRemovedEntry(entry);
            }
            return result;
        }
        for (T entry : first) {
            T s = getSame( second, entry );
            if (s == null) {
                result.putRemovedEntry( entry );
                continue;
            }
            if (comparator == null || comparator.compare( entry, s ) == 0) {
                result.putSameEntry( entry );
            } else {
                result.putChangedEntry( entry, s );
            }
        }
        for (T entry : second) {
            if (!first.contains(entry)) {
                result.putAddedEntry(entry);
            }
        }
        return result;
    }

    private static <T> T getSame( Collection<T> second, T entry ) {
        for (T t : second) {
            if(Objects.equals( entry, t )) return t;
        }

        return null;
    }
}
