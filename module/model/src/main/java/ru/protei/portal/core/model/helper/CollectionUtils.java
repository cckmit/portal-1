package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.marker.HasLongId;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return (null == collection || collection.isEmpty());
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return (null == map || map.isEmpty());
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
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

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return null == iterator ? Stream.empty() :
                StreamSupport.stream(((Iterable<T>)() -> iterator).spliterator(), false);
    }

    public static <T> Stream<T> stream(T[] array) {
        return null == array ? Stream.empty() : Arrays.stream(array);
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

    public static <T> List<T> emptyIfNull( List<T> list ) {
        return list == null ? Collections.<T>emptyList() : list;
    }

    public static <K, V> Map<K, V> emptyIfNull( Map<K, V> map ) {
        return map == null ? Collections.<K, V>emptyMap() : map;
    }

    public static <T> Set<T> nullIfEmpty( Set<T> set) {
        if(isEmpty( set )) {
            return null;
        }
        return set;
    }

    public static <T> List<T> nullIfEmpty( List<T> list) {
        if(isEmpty( list )) {
            return null;
        }
        return list;
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

    public static <R, T> Set<R> toSet( T[] iterable, Function<? super T, ? extends R> mapper  ) {
        Set<R> result = new HashSet<>();
        transform( iterable, result, mapper );
        return result;
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

    public static <K, V> void mergeMap(Map<K, V> accumulatorMap, Map<K, V> map, BiFunction<V, V, V> mergeOperator) {
        map.forEach((key, value) -> accumulatorMap.merge(key, value, mergeOperator));
    }

    public static <T> List<T> singleValueList(T value) {
        List<T> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    public static <T> List<T> unmodifiableListOf(T... elements){
        if(elements == null) return Collections.unmodifiableList(Collections.EMPTY_LIST);
        return Collections.unmodifiableList( Arrays.asList( elements ));
    }

    public static <T> List<T> listOf(T... elements){
        if(elements == null) return new ArrayList<>();
        return new ArrayList<>( Arrays.asList( elements ));
    }

    public static <T> List<T> listOf(Collection<T> elements){
        if(elements == null) return new ArrayList<>();
        return new ArrayList<>( elements );
    }

    public static <T> List<T> listOfOrNull(Collection<T> elements){
        if(elements == null) return null;
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

    public static <T extends HasLongId> List<Long> collectIds(Collection<T> entities) {
        return stream(entities)
                .map(HasLongId::getId)
                .collect(Collectors.toList());
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

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }



    /**
     * Равенство коллекций, без учета пордяка элементов
     */
    public static <T> boolean equals(Collection<T> firstCollection, Collection<T> secondCollection) {
        if (firstCollection == null || secondCollection == null) {
            return firstCollection == null && secondCollection == null;
        }

        if (firstCollection.size() != secondCollection.size()) {
            return false;
        }

        if (!firstCollection.containsAll(secondCollection)) {
            return false;
        }

        if (!secondCollection.containsAll(firstCollection)) {
            return false;
        }

        return true;
    }

    /**
     * Проход по списку в обратном направлении
     */
    public static <T> void forEachReverse(List<T> elements, Consumer<T> elementConsumer) {
        if (isEmpty(elements) || elementConsumer == null) {
            return;
        }

        ListIterator<T> listIterator = elements.listIterator(elements.size());

        while (listIterator.hasPrevious()) {
            elementConsumer.accept(listIterator.previous());
        }
    }

    public static int[] toPrimitiveIntegerArray(List<Integer> elements) {
        if (isEmpty(elements)) {
            return new int[0];
        }

        int[] result = new int[elements.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = elements.get(i);
        }

        return result;
    }

    public static String joining(Iterable<String> iterable, String delimiter) {
        return String.join(delimiter, iterable);
    }

    public static <T> String joining(Iterable<T> iterable, String delimiter, Function<? super T, ? extends String> mapper) {
        List<String> result = new ArrayList<>();
        transform( iterable, result, mapper );
        return joining(result, delimiter);
    }

    public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> resultList = new ArrayList<>();

        if (list1 != null) {
            resultList.addAll(list1);
        }

        if (list2 != null) {
            resultList.addAll(list2);
        }

        return resultList;
    }

    public static <T> List<T> nullsLast(List<T> list, Function<T, ?> mapperToPossibleNull) {
        if (isEmpty(list) || mapperToPossibleNull == null) {
            return list;
        }

        List<T> listWithNulls = new ArrayList<>();
        List<T> result = new ArrayList<>();

        for (T nextObject : list) {
            if (mapperToPossibleNull.apply(nextObject) == null) {
                listWithNulls.add(nextObject);
            } else {
                result.add(nextObject);
            }
        }

        result.addAll(listWithNulls);

        return result;
    }
}
