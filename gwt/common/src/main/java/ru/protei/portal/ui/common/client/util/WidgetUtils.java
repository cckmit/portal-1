package ru.protei.portal.ui.common.client.util;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtils {

    public static HTMLPanel makeDiv() {
        return makeDiv("");
    }

    public static HTMLPanel makeDiv(String html) {
        return new HTMLPanel("div", html);
    }

    public static FocusPanel makeFocusDiv() {
        return new FocusPanel();
    }

    public static FocusPanel makeFocusDiv(Widget child) {
        return new FocusPanel(child);
    }

    public static HTMLPanel makeStyledDiv(String styleName) {
        HTMLPanel div = makeDiv();
        div.addStyleName(styleName);
        return div;
    }

    public static HTMLPanel makeStyledDiv(String styleName, String html) {
        HTMLPanel div = makeDiv(html);
        div.addStyleName(styleName);
        return div;
    }

    public static FocusPanel makeStyledFocusDiv(String styleName) {
        FocusPanel div = makeFocusDiv();
        div.addStyleName(styleName);
        return div;
    }

    public static Image makeImg(String src) {
        Image img = new Image();
        img.setUrl(src);
        return img;
    }
}
