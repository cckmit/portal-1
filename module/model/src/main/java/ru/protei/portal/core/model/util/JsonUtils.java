package ru.protei.portal.core.model.util;

import java.util.List;

public class JsonUtils {
    public static String wrapJsonsToJsonList(List<String> jsons) {
        StringBuilder jsonsList = new StringBuilder("[");

        for (int i = 0; i < jsons.size() - 1; i++) {
            jsonsList.append(jsons.get(i));
            jsonsList.append(",");
        }

        jsonsList.append(jsons.get(jsons.size() - 1));
        jsonsList.append("]");

        return jsonsList.toString();
    }
}
