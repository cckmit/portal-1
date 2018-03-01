package ru.protei.portal.tools.migrate;

import ru.protei.portal.core.model.helper.HelperFunc;
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
        StringBuilder x = new StringBuilder();
        if (HelperFunc.isNotEmpty(lastName)) {
            x.append(lastName).append(" ");
        }

        if (HelperFunc.isNotEmpty(firstName)) {
            x.append(firstName).append(" ");
        }

        if (HelperFunc.isNotEmpty(secondName)) {
            x.append(secondName);
        }

        return x.toString();
    }

    public static String generateDisplayShortName (ExternalPerson person) {
        return generateDisplayShortName(person.getFirstName(), person.getLastName(), person.getSecondName());
    }

    public static String generateDisplayShortName(String firstName, String lastName, String secondName) {
        StringBuilder x = new StringBuilder();
        if (HelperFunc.isNotEmpty(lastName)) {
            x.append(lastName).append(" ");
        }

        if (HelperFunc.isNotEmpty(firstName)) {
            x.append(firstName.charAt(0)).append(".");
        }

        if (HelperFunc.isNotEmpty(secondName)) {
            x.append(secondName.charAt(0)).append(".");
        }

        return x.toString();
    }

}
