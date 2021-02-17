package ru.protei.portal.ui.common.client.util;


import ru.protei.portal.core.model.helper.StringUtils;

/**
 * Утилита по работе цветами
 */
public class ColorUtils {

    static public String makeSingleCharName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        return String.valueOf(name.charAt(0)).toUpperCase();
    }

    static public String makeSafeColor(String color) {
        if (StringUtils.isNotBlank(color)) {
            return color;
        }
        return COLOR_LIGHT_GRAY;
    }

    static public String makeContrastColor(String color) {
        int baseColor = parseHexColor(color);

//        make RGB from baseColor
        int red = (baseColor >> 16) & 255;
        int green = (baseColor >> 8) & 255;
        int blue = baseColor & 255;

        double contrastFormulaResult = red * 0.299 + green * 0.587 + blue * 0.114;

        return contrastFormulaResult > 186 ? COLOR_BLACK : COLOR_WHITE;
    }

    static private int parseHexColor(String color) {
        if (color.charAt(0) == '#') {
            color = color.substring(1);
        }
        if (color.length() != 6 && color.length() != 8) {
            return 0;
        }
        long parsed = Long.parseLong(color, 16);
        if (color.length() == 6) {
            // Set the alpha value
            parsed |= 0x00000000ff000000;
        }
        return (int) parsed;
    }

    private static final String COLOR_LIGHT_GRAY = "#e9edef";
    private static final String COLOR_BLACK = "#000000";
    private static final String COLOR_WHITE = "#FFFFFF";
}
