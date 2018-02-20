package ru.protei.portal.tools.migrate;

import ru.protei.portal.tools.migrate.struct.ExternalPerson;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by turik on 18.08.16.
 */
public class HelperService {

    public static SimpleDateFormat DATE = new SimpleDateFormat ("yyyy-MM-dd");

    public static String generateDisplayName (ExternalPerson person) {
        return generateDisplayName(person.getFirstName(), person.getLastName(), person.getSecondName());
    }

    public static String generateDisplayName(String firstName, String lastName, String secondName) {
        return lastName + " " + firstName + (secondName != null ? (" " + secondName) : "");
    }

    public static String generateDisplayShortName(String firstName, String lastName, String secondName) {
        return lastName + " " + (firstName.charAt (0) + ".") + (secondName != null ? secondName.charAt(0) + "." : "");
    }

    public static boolean equalsNotNull (Number a, Number b) {
        return a != null && b != null && a.equals(b);
    }

    public static <K,T> List<K> keys (List<T> src, Function<T,K> keyExtractor) {
        return src.stream().map(item -> keyExtractor.apply(item)).collect(Collectors.toList());
    }

    public static <K,T> Map<K,T> map (List<T> src, Function<T,K> keyExtractor) {
        Map<K,T> result = new HashMap<>();
        src.forEach(item -> result.put(keyExtractor.apply(item), item));
        return result;
    }

    public static  <T> void splitBatch(List<T> full_list, int batchSize, Consumer<List<T>> consumer) {
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
}
