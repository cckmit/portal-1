package ru.protei.portal.ui.common.client.util;

import java.util.Optional;

public class FilenameUtils {
    
    public static String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse(null);
    }
}
