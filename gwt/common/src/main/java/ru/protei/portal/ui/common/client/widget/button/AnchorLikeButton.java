package ru.protei.portal.ui.common.client.widget.button;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import ru.protei.portal.core.model.helper.StringUtils;

public class AnchorLikeButton extends Button {
    public AnchorLikeButton() {
        addClickHandler(event -> {
            event.stopPropagation();

            String href = getElement().getAttribute("href");

            if (StringUtils.isBlank(href)) {
                return;
            }

            Window.open(href, null, null);
        });
    }

    public void setHref(String href) {
        getElement().setAttribute("href", href);
    }
}
