package ru.protei.portal.util;

import java.net.URI;

public class UriUtils {

    public static String getLastPathSegment(URI uri) {
        if (uri == null)
            return null;
        String path = uri.getPath();
        int i = path.lastIndexOf("/");
        if (i < 0 || i >= path.length())
            return null;
        return path.substring(i + 1);
    }
}
