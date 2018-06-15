package ru.protei.portal.core.model.helper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by michael on 06.07.16.
 */
public class HelperFunc {

    public static Long toTime (Date t, Long v) {
        return t == null ? v : t.getTime();
    }

    public static boolean isEmpty (String s) {
        return s == null || s.trim().isEmpty();
    }


    public static boolean isNotEmpty (String s) {
        return !isEmpty(s);
    }

    public static boolean equals (Object o1, Object o2) {
        return o1 == null ? o2 == null : o2 != null ? (o1 == o2 || o1.equals(o2)) : false;
    }

    public static boolean allEquals (Object...objects) {
        if (objects == null || objects.length == 0)
            return true;

        Object cmp = objects[0];

        for (Object o : objects) {
            if (!equals(cmp, o))
                return false;
        }

        return true;
    }

    public static String joinNotEmpty (String delim, String... arr) {
        return Arrays.stream(arr).filter(s -> isNotEmpty(s)).collect(Collectors.joining(delim));
    }

    public static String join(String delim, Collection<String> collection) {
        return collection.stream().collect(Collectors.joining(delim));
    }

    public static boolean testAllNotEmpty (String...arr) {
        for (String s : arr)
            if (isEmpty(s))
                return false;

        return true;
    }

    public static <T> T nvlt (T...arr) {
        for (T t : arr)
            if (t != null)
                return t;
        return null;
    }

    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }

    public static boolean isLikeRequired (String arg) {
        return isNotEmpty(arg) && !arg.equals("%");
    }

    public static String makeLikeArg (String arg) {
        return makeLikeArg (arg, false);
    }

    public static String makeLikeArg (String arg, boolean leftSideAny) {
        if (arg == null || arg.isEmpty()) {
            return "%";
        }

        if (leftSideAny && !arg.startsWith("%"))
            arg = "%" + arg;

        if (!arg.endsWith("%"))
            arg = arg + "%";

        return arg;
    }

    public static <T> Collection<T> subtract(Collection<T> a, Collection<T> b){
        if(b == null || b.isEmpty() || a == null || a.isEmpty())
            return a;

        Collection<T> result = new HashSet<>();
        for(T ca: a){
            if(!b.contains(ca))
                result.add(ca);
        }

        return result;
    }

    public static boolean equalsNotNull (Number a, Number b) {
        return a != null && b != null && a.equals(b);
    }

    public static <E> E last (List<E> list) {
        return list.isEmpty() ? null : list.get(list.size()-1);
    }

    public static <E extends Enum<E>> E find(Class<E> enumClass, E def, Predicate<E> predicate) {
        for (E e : enumClass.getEnumConstants())
            if (predicate.test(e))
                return e;

        return def;
    }

    public static <K extends Comparable,T> K max (List<T> src, Function<T,K> valueFunc) {
        T data = (T)src.stream().max(Comparator.comparing(valueFunc::apply)).get();
        return valueFunc.apply(data);
    }

    public static <K,T> List<K> keys (List<T> src, Function<T,K> keyExtractor) {
        List<K> keys = new ArrayList<>(src.size());
        for (T item : src) {
            K key = keyExtractor.apply(item);
            if (key != null)
                keys.add(key);
        }
        return keys;
    }

    public static <K,T> Map<K,T> map (List<T> src, Function<T,K> keyExtractor) {
        Map<K,T> result = new HashMap<>();
        src.forEach(item -> result.put(keyExtractor.apply(item), item));
        return result;
    }

    public static  <T> void splitBatch(List<T> full_list, int batchSize, Consumer<List<T>> consumer) {
        if (full_list.isEmpty())
            return;

        int full_batches = full_list.size()/batchSize;

        if (full_batches == 0)
            consumer.accept(full_list);
        else {
            for (int i = 0; i < full_batches; i++) {
                consumer.accept(full_list.subList(i * batchSize, (i + 1) * batchSize));
            }
            // rest
            consumer.accept(full_list.subList(full_batches*batchSize, full_list.size()));
        }
    }

    public static String makeInArg(Collection<?> col) {
        return "(" +
                col.stream()
                        .map(s -> "'" + s + "'")
                        .collect(Collectors.joining(","))
                + ")";
    }

    public static String makeInArg(Collection<?> col, boolean needQuotation) {
        if (needQuotation) {
            return makeInArg(col);
        }
        return "(" +
                col.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))
                + ")";
    }

    /**
     * nullGreater - если значение null то трактовать как большее
     */
    public static <T extends Comparable<? super T>> int compare( T c1, T c2, boolean nullGreater ) {
        return compare(c1, c2, nullGreater, null);
    }

    /**
     * nullGreater - если значение null то трактовать как большее
     */
    public static <T extends Comparable<? super T>> int compare( T c1, T c2, boolean nullGreater, Comparator<T> comparator ) {
        if ( c1 == c2 ) {
            return 0;
        }
        else if ( c1 == null ) {
            return nullGreater ? 1 : -1;
        }
        else if ( c2 == null ) {
            return nullGreater ? -1 : 1;
        }

        if (comparator != null) {
            return comparator.compare(c1, c2);
        } else {
            return c1.compareTo(c2);
        }
    }
}
