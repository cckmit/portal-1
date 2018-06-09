package ru.protei.portal.util;

import java.io.UnsupportedEncodingException;

public class StringUtils {
    public static String encodeToRFC2231(String value) {
        StringBuilder buf = new StringBuilder();
        byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // cannot happen with UTF-8
            bytes = new byte[]{ '?' };
        }
        for (byte b : bytes) {
            if (b < '+' || b == ';' || b == ',' || b == '\\' || b > 'z') {
                buf.append('%');
                String s = Integer.toHexString(b & 0xff).toUpperCase();
                if (s.length() < 2) {
                    buf.append('0');
                }
                buf.append(s);
            } else {
                buf.append((char) b);
            }
        }
        return buf.toString();
    }
}
