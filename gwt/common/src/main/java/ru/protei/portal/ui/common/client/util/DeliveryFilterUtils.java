package ru.protei.portal.ui.common.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public class DeliveryFilterUtils {
    public static final RegExp serialNumbersPattern = RegExp.compile("(\\d{3}.\\d{3},?)+");

    public static List<String> searchSerialNumber(String searchString) {
        if (isBlank(searchString)) {
            return null;
        }

        MatchResult result = serialNumbersPattern.exec(searchString);
        if (result != null && result.getGroup(0).equals(searchString)) {
            return Arrays.stream(searchString.split(","))
                    .collect(Collectors.toList());
        }

        return null;

    }
}
